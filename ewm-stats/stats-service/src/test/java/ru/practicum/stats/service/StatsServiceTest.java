package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceTest {
    private final StatsService statsService;

    @Test
    public void shouldSaveHitAndGetStats() {
        LocalDateTime dateTime = LocalDateTime.now().minusYears(1);

        EndpointHit hit1 = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.168.0.1")
                .timestamp(dateTime).build();
        EndpointHit hit2 = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.168.0.1")
                .timestamp(dateTime.plusDays(15)).build();
        statsService.saveHit(hit1);
        statsService.saveHit(hit2);

        List<ViewStats> stats = statsService.getStats(dateTime.minusDays(15), dateTime.plusDays(30), null, false);
        assertThat(stats.size(), is(2));

        stats = statsService.getStats(dateTime.minusDays(30), dateTime.plusDays(30), null, true);
        assertThat(stats.size(), is(2));
    }
}