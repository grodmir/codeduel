package com.codeduel.codeduelsandbox.docker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Подтягивает properties из application.yml и предоставляет к ним доступ
 */
@Data
@Component
@ConfigurationProperties(prefix = "sandbox.docker")
public class DockerProperties {
    private String host;
    private boolean tlsVerify;
    private String certPath;
    private int connectTimeout;
    private int responseTimeout;
    private int maxConnections;
}
