package com.codeduel.codeduelsandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Frame;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  Интеграционный тест, который проверяет, что контекст Spring корректно поднимает DockerClient.
 *  С помощью клиента подключаемся к Docker-демону хоста, запускаем тестовый контейнер с java JDK
 *  и командой "java -version"
 *  <p>
 *  Требует запущенного Docker Engine
 *  Не запускается автоматически при "mvn test"
 */

@Slf4j
@SpringBootTest
public class DockerConnectivityIT {

    @Autowired
    private DockerClient dockerClient;

    // Через DockerClient перепроверяет наличие image и докачивает его, если нужно
    private void ensureImagePresent(DockerClient dockerClient, String imageName) {
        try {
            dockerClient.inspectImageCmd(imageName).exec();
        } catch (NotFoundException e) {
            log.info("Image {} not found locally, pulling...", imageName);
            try {
                dockerClient.pullImageCmd(imageName)
                        .exec(new PullImageResultCallback())
                        .awaitCompletion(5, TimeUnit.MINUTES);
            } catch (InterruptedException ex) {
                log.error("Image pulling was interrupted", ex);
                Thread.currentThread().interrupt();
                throw new RuntimeException(ex);
            }
            log.info("Image {} pulled successfully", imageName);
        }
    }

    @Test
    void shouldRunJavaVersionInContainer() throws Exception {
        String image = "eclipse-temurin:17-jdk-jammy";
        ensureImagePresent(dockerClient, image);
        CreateContainerResponse container = null;

        try {
            // Создаем контейнер (без запуска) с image джавы, с командой "java -version",
            // флаги withAttachStdout, withAttachStderr говорят Docker буферизировать вывод
            container = dockerClient.createContainerCmd(image)
                    .withCmd("java", "-version")
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            String containerId = container.getId();
            dockerClient.startContainerCmd(containerId).exec();

            int exitCode;

            // Ожидаем завершения контейнера, коллбэк WaitContainerResultCallback запишет в себя результат выполнения
            try (WaitContainerResultCallback waitCallback = new WaitContainerResultCallback()) {
                dockerClient.waitContainerCmd(containerId).exec(waitCallback);
                exitCode = waitCallback.awaitStatusCode(30, TimeUnit.SECONDS);
            }

            StringBuilder output = new StringBuilder();

            // Нужно прочитать логи контейнера, поэтому нужно переопределить свой обработчик для коллбэка
            // onNext() определяет, что мы делаем с каждым фреймом данных из потоков.
            // Тут просто пишем в output все фреймы подряд
            try (ResultCallback.Adapter<Frame> logCallback = new ResultCallback.Adapter<>() {
                @Override
                public void onNext(Frame frame) {
                    output.append(new String(frame.getPayload()));
                }
            }) {
                // Вызываем команду logs с нашими буферизированными логами, просим и stdout, и stderr
                // logCallback запишет все в output
                dockerClient.logContainerCmd(containerId)
                        .withStdOut(true)
                        .withStdErr(true)
                        .exec(logCallback)
                        .awaitCompletion(10, TimeUnit.SECONDS);
            }

            assertThat(exitCode).isZero();
            assertThat(output.toString().toLowerCase().contains("openjdk"));
        } finally {
            // В конце через DockerClient удаляем контейнер, т.к. он не наш объект и через AutoClosable его не закрыть
            if (container != null) {
                dockerClient.removeContainerCmd(container.getId())
                        .withForce(true)
                        .exec();
            }
        }
    }
}
