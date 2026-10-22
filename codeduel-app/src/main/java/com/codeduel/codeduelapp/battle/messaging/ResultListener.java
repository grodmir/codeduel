package com.codeduel.codeduelapp.battle.messaging;

import com.codeduel.QueueNames;
import com.codeduel.SubmissionResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Класс, который слушает очередь и вызывает сервисную логику для дальнейшей работы приложения.
 * <p>
 * На данный момент класс только выводит результат до момента, пока не появится настоящий сервис
 * */
@Slf4j
@Component
public class ResultListener {

    @RabbitListener(queues = QueueNames.RESULTS_QUEUE)
    public void handle(SubmissionResultMessage result) {
        log.info("Received result: submissionId={}, matchId={}, verdict={}",
                result.submissionId(), result.matchId(), result.verdict());
        // TODO - далее логика battle
    }
}
