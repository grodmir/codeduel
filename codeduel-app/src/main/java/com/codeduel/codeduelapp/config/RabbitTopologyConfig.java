package com.codeduel.codeduelapp.config;

import com.codeduel.QueueNames;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Проверяет наличие соответствующих очередей сообщение в брокере сообщение
 * и в случае их отсутствия, создает их.
 * */
@Configuration
public class RabbitTopologyConfig {

    @Bean
    public Queue submissionsQueue() {
        return QueueBuilder.durable(QueueNames.SUBMISSIONS_QUEUE).build();
    }

    @Bean
    public Queue resultsQueue() {
        return QueueBuilder.durable(QueueNames.RESULTS_QUEUE).build();
    }
}
