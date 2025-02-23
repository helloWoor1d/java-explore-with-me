package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository repository;

    public void saveHit(EndpointHit hit) {
        log.debug("Добавлены данные о запросе app = {}, uri = {}", hit.getApp(), hit.getUri());
        repository.save(hit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    List<String> uris, Boolean unique) {
        log.debug("Собрана статистика за период {} - {}. Только уникальные посещения ({})", start, end, unique);
        if (uris == null || uris.isEmpty()) {
            if (unique) return repository.getAllStatsWithUniqueIp(start, end);
            else return repository.getAllStats(start, end);
        } else {
            if (unique) return repository.getStatsWithUniqueIp(start, end, uris);
            else return repository.getStatsByUris(start, end, uris);
        }
    }
}
