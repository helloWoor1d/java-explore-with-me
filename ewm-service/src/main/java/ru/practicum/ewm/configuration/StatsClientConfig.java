package ru.practicum.ewm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.client.StatsClient;

@Configuration
public class StatsClientConfig {
    @Bean
    public StatsClient statsClient(WebClient webClient) {
        return new StatsClient(webClient);
    }

    @Bean
    public WebClient webClient(@Value("${stats-service.url}") String url) {
        System.out.println("Connecting to Stats Service at: " + url);
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
