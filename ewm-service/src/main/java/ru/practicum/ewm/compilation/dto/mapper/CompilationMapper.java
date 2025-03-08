package ru.practicum.ewm.compilation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.mapper.EventDtoMapperImpl;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = EventDtoMapperImpl.class)
public interface CompilationMapper {
    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDto(List<Compilation> compilations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    Compilation fromDto(NewCompilationDto newCompilationDto, List<Event> events);

    @Mapping(target = "id", source = "compId")
    @Mapping(target = "events", source = "events")
    Compilation fromDto(long compId, UpdateCompilationRequest updateCompilationRequest, List<Event> events);
}
