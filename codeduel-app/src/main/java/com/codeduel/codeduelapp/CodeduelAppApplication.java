package com.codeduel.codeduelapp;

import com.codeduel.codeduelapp.matchmaking.config.MatchmakingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MatchmakingProperties.class)
public class CodeduelAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeduelAppApplication.class, args);
    }

}
