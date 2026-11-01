package com.codeduel.codeduelapp.matchmaking.domain.service;

import com.codeduel.codeduelapp.matchmaking.domain.exception.AlreadyInQueueOrMatchException;
import com.codeduel.codeduelapp.matchmaking.domain.exception.WsNotConnectedException;
import com.codeduel.codeduelapp.matchmaking.domain.model.*;
import com.codeduel.codeduelapp.matchmaking.domain.port.ActiveMatchChecker;
import com.codeduel.codeduelapp.matchmaking.domain.port.MatchFoundListener;
import com.codeduel.codeduelapp.matchmaking.domain.port.MatchmakingNotifier;
import com.codeduel.codeduelapp.matchmaking.domain.port.PlayerRatingProvider;
import com.codeduel.codeduelapp.matchmaking.domain.repository.QueueStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final QueueStore queueStore;
    private final PairFinder pairFinder;
    private final PlayerRatingProvider ratingProvider;
    private final ActiveMatchChecker activeMatchChecker;
    private final MatchFoundListener matchFoundListener;
    private final MatchmakingNotifier notifier;
    private final Clock clock;

    private final ReentrantLock lock = new ReentrantLock();
    private final Set<Integer> pairing = new HashSet<>();

    /**
     * Ставит игрока в очередь.
     *
     * @throws WsNotConnectedException         нет живой WS-сессии
     * @throws AlreadyInQueueOrMatchException игрок уже в очереди, в формировании пары или в матче
     */
    public void join(int userId) {
        if (!notifier.isOnline(userId)) {
            throw new WsNotConnectedException(userId);
        }
        // Обращения к БД идут вне блокировки. Оставшаяся узкая гонка (матч успел создаться между этой
        // проверкой и добавлением в очередь) закрывается проверкой в транзакции создания матча в battle.
        if (activeMatchChecker.hasActiveMatch(userId)) {
            throw new AlreadyInQueueOrMatchException(userId);
        }
        int rating = ratingProvider.getRatingByUserId(userId);
        QueueEntry entry = new QueueEntry(userId, rating, clock.instant());

        runLocked(() -> {
            if (pairing.contains(userId) || !queueStore.add(entry)) {
                throw new AlreadyInQueueOrMatchException(userId);
            }
        });
        log.info("User {} joined the queue with rating {}", userId, rating);
    }

    /** Отмена поиска. Победитель гонки с подбором определяется тем, кто первым взял блокировку. */
    public LeaveResult leave(int userId) {
        LeaveResult result = callLocked(() -> {
            if (queueStore.remove(userId)) {
                return LeaveResult.REMOVED;
            }
            return pairing.contains(userId) ? LeaveResult.ALREADY_MATCHED : LeaveResult.NOT_IN_QUEUE;
        });
        log.info("User {} left the queue: {}", userId, result);
        return result;
    }

    public PlayerStatus status(int userId) {
        boolean searching = callLocked(() -> queueStore.find(userId).isPresent() || pairing.contains(userId));
        if (searching) {
            return PlayerStatus.SEARCHING;
        }
        return activeMatchChecker.hasActiveMatch(userId) ? PlayerStatus.IN_MATCH : PlayerStatus.IDLE;
    }

    /** Вызывается при закрытии текущей WS-сессии игрока: убирает его из очереди (если он там). */
    public void onPlayerDisconnected(int userId) {
        leave(userId);
    }

    /** Один проход подбора. Вызывается планировщиком, повторно не входит (fixedDelay). */
    public void tick() {
        List<MatchedPair> pairs = takePairs();
        for (MatchedPair pair : pairs) {
            process(pair);
        }
    }

    /**
     * Под блокировкой: убирает из очереди игроков без живой сессии, ищет пары, изымает игроков из очереди
     * и помечает их как pairing.
     */
    private List<MatchedPair> takePairs() {
        return callLocked(() -> {
            List<QueueEntry> online = new ArrayList<>();
            for (QueueEntry entry : queueStore.snapshot()) {
                if (notifier.isOnline(entry.userId())) {
                    online.add(entry);
                } else {
                    queueStore.remove(entry.userId());
                    log.info("User {} removed from the queue: no active WebSocket session", entry.userId());
                }
            }

            List<MatchedPair> pairs = pairFinder.findPairs(online, clock.instant());
            for (MatchedPair pair : pairs) {
                for (int userId : new int[]{pair.first().userId(), pair.second().userId()}) {
                    queueStore.remove(userId);
                    pairing.add(userId);
                }
            }
            return pairs;
        });
    }

    /** Вне блокировки: создаёт матч для пары и уведомляет игроков. Сбой одной пары не влияет на другие. */
    private void process(MatchedPair pair) {
        int first = pair.first().userId();
        int second = pair.second().userId();

        Integer matchId = null;
        try {
            matchId = matchFoundListener.onMatchFound(pair);
        } catch (RuntimeException e) {
            log.error("Failed to create a match for users {} and {}", first, second, e);
        } finally {
            // После успеха игрока защищает ACTIVE-запись в Postgres, после сбоя защита не нужна.
            runLocked(() -> {
                pairing.remove(first);
                pairing.remove(second);
            });

        }

        if (matchId != null) {
            log.info("Match {} created for users {} and {}", matchId, first, second);
            notifyFound(first, matchId, second);
            notifyFound(second, matchId, first);
        } else {
            notifyFailed(first);
            notifyFailed(second);
        }
    }

    private void notifyFound(int userId, int matchId, int opponentId) {
        try {
            notifier.matchFound(userId, matchId, opponentId);
        } catch (RuntimeException e) {
            log.warn("Could not deliver MATCH_FOUND to user {}", userId, e);
        }
    }

    private void notifyFailed(int userId) {
        try {
            notifier.matchFailed(userId, MatchFailureReason.MATCH_CREATION_FAILED);
        } catch (RuntimeException e) {
            log.warn("Could not deliver MATCH_FAILED to user {}", userId, e);
        }
    }

    private <T> T callLocked(Supplier<T> action) {
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    private void runLocked(Runnable action) {
        lock.lock();
        try {
            action.run();
        }  finally {
            lock.unlock();
        }
    }
}
