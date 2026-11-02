package com.codeduel.codeduelapp.matchmaking.domain.port;

import com.codeduel.codeduelapp.matchmaking.domain.model.MatchedPair;

/**
 * Порт к battle: создать матч для сформированной пары. Реализует battle.
 * <p>
 * Контракт реализации:
 * <ul>
 *   <li>вызывается из потока планировщика, вне блокировок matchmaking и вне какой-либо транзакции;</li>
 *   <li>создаёт матч в собственной транзакции <b>с таймаутом</b> (например, {@code @Transactional(timeout = 5)})
 *       и коммитит её <b>до возврата</b> из метода: после возврата {@code ActiveMatchChecker}
 *       уже обязан видеть матч со статусом ACTIVE;</li>
 *   <li>в той же транзакции проверяет, что ни у одного из игроков нет ACTIVE-матча,
 *       и при нарушении бросает исключение (страховка от гонки при {@code join});</li>
 *   <li>при любом сбое бросает {@link RuntimeException} и не оставляет частичного состояния;</li>
 *   <li>возвращает идентификатор созданного матча.</li>
 * </ul>
 */
public interface MatchFoundListener {

    int onMatchFound(MatchedPair pair);
}

