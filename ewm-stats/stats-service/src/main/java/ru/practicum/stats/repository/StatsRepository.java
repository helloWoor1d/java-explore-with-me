package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.stats.model.ViewStats(sh.app, sh.uri, COUNT(*)) " +
            "FROM EndpointHit AS sh " +
            "WHERE sh.timestamp <= :end " +
            "AND sh.timestamp >= :start " +
            "AND sh.uri IN :uris " +
            "GROUP BY sh.app, sh.uri " +
            "ORDER BY COUNT(*) DESC ")
    List<ViewStats> getStatsByUris(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.model.ViewStats(sh.app, sh.uri, COUNT(DISTINCT sh.ip)) " +
            "FROM EndpointHit AS sh " +
            "WHERE sh.timestamp <= :end " +
            "AND sh.timestamp >= :start " +
            "AND sh.uri IN :uris " +
            "GROUP BY sh.app, sh.uri " +
            "ORDER BY COUNT(DISTINCT sh.ip) DESC ")
    List<ViewStats> getStatsByUrisWithUniqueIp(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.stats.model.ViewStats(sh.app, sh.uri, COUNT(DISTINCT sh.ip)) " +
            "FROM EndpointHit AS sh " +
            "WHERE sh.timestamp <= :end " +
            "AND sh.timestamp >= :start " +
            "GROUP BY sh.app, sh.uri " +
            "ORDER BY COUNT(DISTINCT sh.ip) DESC ")
    List<ViewStats> getStatsByDateWithUniqueIp(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.model.ViewStats(sh.app, sh.uri, COUNT(*)) " +
            "FROM EndpointHit AS sh " +
            "WHERE sh.timestamp <= :end " +
            "AND sh.timestamp >= :start " +
            "GROUP BY sh.app, sh.uri " +
            "ORDER BY COUNT(*) DESC ")
    List<ViewStats> getStatsByDate(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);
}
