package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.model.EndpointHitDto;
import ru.practicum.stats.model.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.model.ViewStats;

import java.util.List;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMAT;

@Mapper(componentModel = "spring")
public interface HitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = DATE_TIME_FORMAT)
    EndpointHit fromDto(EndpointHitDto dto);

    ViewStatsDto toDto(ViewStats stats);

    List<ViewStatsDto> toDto(List<ViewStats> stats);
}
