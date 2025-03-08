package ru.practicum.ewm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.client.StatsClient;

@Configuration
public class StatsClientConfig {
    @Bean
    public StatsClient statsClient() {
        return new StatsClient(
                WebClient.builder().baseUrl("http://localhost:9090").build());
    }
}
