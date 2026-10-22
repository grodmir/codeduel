package com.codeduel.codeduelsandbox.config;

import com.codeduel.codeduelsandbox.docker.DockerProperties;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 *  Настраивает бин DockerClient, с помощью которого происходит работа с Docker-демоном на хосте.
 *  Передает настройки конфигурации из application.yml через property-класс в конфигурацию Docker
 *  и в настройки транспортного клиента HTTP, который управляет соединением с Docker-демоном
 * */
@Configuration
@RequiredArgsConstructor
public class DockerConfig {

    private final DockerProperties properties;

    @Bean
    public DockerClient dockerClient() {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(properties.getHost())
                .withDockerTlsVerify(properties.isTlsVerify());

        if (properties.getCertPath() != null && !properties.getCertPath().isBlank()) {
            configBuilder.withDockerCertPath(properties.getCertPath());
        }

        DockerClientConfig config = configBuilder.build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(properties.getMaxConnections())
                .connectionTimeout(Duration.ofMillis(properties.getConnectTimeout()))
                .responseTimeout(Duration.ofMillis(properties.getResponseTimeout()))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
