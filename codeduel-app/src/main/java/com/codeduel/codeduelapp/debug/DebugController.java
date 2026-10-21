package com.codeduel.codeduelapp.debug;

import com.codeduel.SubmissionTaskMessage;
import com.codeduel.codeduelapp.battle.SubmissionProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Временный класс для проверки работоспособности сервисов с RabbitMQ
 * */
@Slf4j
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final SubmissionProducer submissionProducer;

    @PostMapping("/submit")
    public Map<String, String> submit(@RequestBody String code) {
        String submissionId = UUID.randomUUID().toString();
        submissionProducer.send(new SubmissionTaskMessage(
                1,
                submissionId,
                UUID.randomUUID().toString(),
                "debug-user",
                "debug-problem",
                code,
                Instant.now().toString()
        ));
        log.info("submissionId={}", submissionId);
        return Map.of("submissionId", submissionId);
    }
}
