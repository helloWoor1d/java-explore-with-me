package ru.practicum.ewm.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.client.StatsClient;

@Configuration
@Slf4j
public class StatsClientConfig {
    @Bean
    public StatsClient statsClient(WebClient webClient) {
        return new StatsClient(webClient);
    }

    @Bean
    public WebClient webClient(@Value("${stats-service.url}") String url) {
        log.debug("Connecting to Stats Service at: {}", url);
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
