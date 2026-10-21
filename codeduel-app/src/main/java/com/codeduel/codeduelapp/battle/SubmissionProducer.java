package com.codeduel.codeduelapp.battle;

import com.codeduel.QueueNames;
import com.codeduel.SubmissionTaskMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Публикует решенную задачу в соответствующую очередь сообщений
 * */
@Slf4j
@Component
public class SubmissionProducer {

    private final RabbitTemplate rabbitTemplate;

    public SubmissionProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(SubmissionTaskMessage message) {
        log.info("Sending Submission Message from user:{} for problem: {}", message.userId(), message.problemId());
        rabbitTemplate.convertAndSend(QueueNames.SUBMISSIONS_QUEUE, message);
    }
}
