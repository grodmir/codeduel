package com.codeduel.codeduelsandbox;

import com.codeduel.QueueNames;
import com.codeduel.SubmissionResultMessage;
import com.codeduel.SubmissionTaskMessage;
import com.codeduel.Verdict;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Симуляция реальной работы Sandbox. Возвращает всегда ACCEPTED на любой код
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class SubmissionListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = QueueNames.SUBMISSIONS_QUEUE)
    public void handle(SubmissionTaskMessage task,
                       Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("Received submission: submissionId={}, matchId={}, userId={}",
                task.submissionId(), task.matchId(), task.userId());

        SubmissionResultMessage result = new SubmissionResultMessage(
                1,
                task.submissionId(),
                task.matchId(),
                task.userId(),
                Verdict.ACCEPTED,
                null, null,
                Instant.now().toString()
        );

        rabbitTemplate.convertAndSend(QueueNames.RESULTS_QUEUE, result);

        channel.basicAck(deliveryTag, false);

        log.info("Result sent: submissionId={}, verdict={}",
                task.submissionId(), result.verdict());
    }
}
