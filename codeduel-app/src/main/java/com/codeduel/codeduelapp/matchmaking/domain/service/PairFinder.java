package com.codeduel.codeduelapp.matchmaking.domain.service;

import com.codeduel.codeduelapp.matchmaking.domain.model.MatchedPair;
import com.codeduel.codeduelapp.matchmaking.domain.model.QueueEntry;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class PairFinder {

    private static final Comparator<QueueEntry> BY_RATING =
            Comparator.comparingInt(QueueEntry::rating).thenComparingInt(QueueEntry::userId);

    /** Кто раньше вошёл в очередь, тот приоритетнее. */
    private static final Comparator<QueueEntry> BY_PRIORITY =
            Comparator.comparing(QueueEntry::joinedAt).thenComparingInt(QueueEntry::userId);

    private final SearchWindow searchWindow;

    public PairFinder(SearchWindow searchWindow) {
        this.searchWindow = searchWindow;
    }

    public List<MatchedPair> findPairs(List<QueueEntry> entries, Instant now) {
        Objects.requireNonNull(entries);
        Objects.requireNonNull(now);
        if (entries.size() < 2) {
            return Collections.emptyList();
        }

        List<QueueEntry> byPriority = new ArrayList<>(entries);
        byPriority.sort(BY_PRIORITY);

        NavigableSet<QueueEntry> available = new TreeSet<>(BY_RATING);
        available.addAll(entries);

        List<MatchedPair> pairs = new ArrayList<>();
        for (QueueEntry seeker : byPriority) {
            if (!available.remove(seeker)) {
                continue;
            }

            findPartner(seeker, available, now).ifPresent(partner -> {
                available.remove(partner);
                pairs.add(new MatchedPair(seeker, partner));
            });
        }
        return List.copyOf(pairs);
    }

    private Optional<QueueEntry> findPartner(QueueEntry seeker, NavigableSet<QueueEntry> available, Instant now) {
        int seekerWindow = windowOf(seeker, now);

        QueueEntry below = firstCompatible(
                available.headSet(seeker, false).descendingIterator(), seeker, seekerWindow, now
        );
        QueueEntry above = firstCompatible(
                available.tailSet(seeker, false).iterator(), seeker, seekerWindow, now
        );


        if (below == null) {
            return Optional.ofNullable(above);
        }
        if (above == null) {
            return Optional.of(below);
        }
        int diffBelow = seeker.rating() - below.rating();
        int diffAbove = above.rating() - seeker.rating();
        if (diffBelow != diffAbove) {
            return Optional.of(diffBelow < diffAbove ? below : above);
        }
        return Optional.of(BY_PRIORITY.compare(below, above) <= 0 ? below : above);
    }

    /**
     * Идёт от искателя в одну сторону по возрастанию разницы рейтингов и возвращает первого подходящего.
     * Дальше окна самого искателя искать бессмысленно: min(окон) не больше окна искателя.
     * Ближайший по рейтингу сосед может не подойти (у него маленькое окно), поэтому идём дальше.
     */
    private QueueEntry firstCompatible(Iterator<QueueEntry> candidates, QueueEntry seeker,
                                       int seekerWindow, Instant now) {
        while (candidates.hasNext()) {
            QueueEntry candidate = candidates.next();
            int diff = Math.abs(candidate.rating() - seeker.rating());
            if (diff > seekerWindow) {
                return null;
            }
            if (diff <= windowOf(candidate, now)) {
                return candidate;
            }
        }
        return null;
    }

    private int windowOf(QueueEntry entry, Instant now) {
        return searchWindow.sizeFor(Duration.between(entry.joinedAt(), now));
    }
}
