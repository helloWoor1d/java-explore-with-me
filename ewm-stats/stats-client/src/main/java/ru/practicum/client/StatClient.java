package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.stats.model.EndpointHitDto;
import ru.practicum.stats.model.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class StatClient {
    private final WebClient webClient;

    public StatClient() {
        webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.displayName())
                .build();
    }

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
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {})
                .doOnError(err -> log.warn("Ошибка отправки запроса на получение статистики - start {}. end {}, uris {}, unique {}. " +
                                "Error: {}", start, end, uris, unique, err.getMessage()))
                .block();
        return response;
    }

    public List<ViewStatsDto> getStats(String start, String end) {
        List<ViewStatsDto> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {})
                .doOnError(err -> log.warn("Ошибка отправки запроса на получение статистики - start {}. end {}. " +
                        "Error: {}", start, end, err.getMessage()))
                .block();
        return response;
    }

    public List<ViewStatsDto> getStats(String start, String end, Boolean unique) {
        List<ViewStatsDto> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {})
                .doOnError(err -> log.warn("Ошибка отправки запроса на получение статистики - start {}. end {}, unique {}. " +
                        "Error: {}", start, end, unique, err.getMessage()))
                .block();
        return response;
    }
}
