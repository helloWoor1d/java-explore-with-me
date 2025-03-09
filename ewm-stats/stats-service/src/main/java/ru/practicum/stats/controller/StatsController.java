package ru.practicum.stats.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.EndpointHitDto;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.model.ViewStatsDto;
import ru.practicum.stats.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMATTER;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final HitMapper mapper;
    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto dto) {
        log.debug("controller: запрос на добавление статистики stats {}", dto);
        EndpointHit hit = mapper.fromDto(dto);
        service.saveHit(hit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.debug("controller: запрос на выборку статистики start: {}, end: {}, uris {}, unique {}", start, end, uris, unique);
        LocalDateTime startDate = LocalDateTime.parse(
                URLDecoder.decode(start, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(
                URLDecoder.decode(end, StandardCharsets.UTF_8), DATE_TIME_FORMATTER);

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Дата начала выборки должна быть до даты конца");
        }

        List<ViewStats> stats = service.getStats(startDate, endDate, uris, unique);
        return stats.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
