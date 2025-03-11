package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.model.EndpointHitDto;
import ru.practicum.stats.model.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class StatsClient {
    private final WebClient webClient;

    public void hit(String app, String uri,
                    String ip, String timestamp) {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp)
                .build();

        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(err -> log.warn("Ошибка при отправке запроса на сервис статистики. {}", err.getMessage()))
                .block();
    }

    public List<ViewStatsDto> getStats(String start, String end,
                                       List<String> uris, Boolean unique) {
        List<ViewStatsDto> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uri", uris)
                        .queryParam("unique", unique)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {})
                .doOnError(err -> log.warn("Ошибка отправки запроса на получение статистики - start {}. end {}, uris {}, unique {}. " +
                                "Error: {}", start, end, uris, unique, err.getMessage()))
                .block();
        log.debug("Получена статистика по параметрам start: {}, end {}, uris {}, unique {}", start, end, uris, unique);
        return response;
    }
}
