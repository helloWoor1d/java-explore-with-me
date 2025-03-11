package ru.practicum.ewm.event.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.category.dto.mapper.CategoryMapperImpl;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventFullWithViews;
import ru.practicum.ewm.event.model.EventShort;
import ru.practicum.ewm.event.model.EventShortWithViews;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryService.class, UserService.class, CategoryMapperImpl.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventDtoMapper {
    @Mapping(target = "initiator", source = "userId")
    @Mapping(target = "category", source = "dto.category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")

    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "paid", source = "dto.paid", defaultValue = "false")
    @Mapping(target = "participantLimit", source = "dto.participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "dto.requestModeration", defaultValue = "true")
    Event fromDto(NewEventDto dto, Long userId);

    @Mapping(target = "category", source = "event.category")
    EventFullDto toFullDto(Event event);

    List<EventFullDto> toFullDto(List<Event> events);

    List<EventShortDto> toEventShortsDto(List<EventShort> events);

    List<EventShortDto> fromEventToShortDto(List<Event> events);

    Event fromUpdateDto(UpdateEventUserRequest request);

    List<EventShortDto> toEventShortDto(List<EventShortWithViews> event);

    EventFullDto toEventFullDto(EventFullWithViews event);

    List<EventFullDto> toEventFullDto(List<EventFullWithViews> event);
}
