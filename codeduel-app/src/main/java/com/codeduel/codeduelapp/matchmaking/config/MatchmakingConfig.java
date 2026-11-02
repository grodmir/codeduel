package com.codeduel.codeduelapp.matchmaking.config;

import com.codeduel.codeduelapp.matchmaking.domain.service.PairFinder;
import com.codeduel.codeduelapp.matchmaking.domain.service.SearchWindow;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MatchmakingProperties.class)
public class MatchmakingConfig {
    @Bean
    public SearchWindow searchWindow(MatchmakingProperties properties) {
        return new SearchWindow(
                properties.baseWindow(),
                properties.windowStepPerSecond(),
                properties.maxWindow());
    }

    @Bean
    public PairFinder pairFinder(SearchWindow searchWindow) {
        return new PairFinder(searchWindow);
    }
}
