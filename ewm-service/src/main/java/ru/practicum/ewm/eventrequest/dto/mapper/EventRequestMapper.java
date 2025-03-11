package ru.practicum.ewm.eventrequest.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.eventrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.eventrequest.model.EventRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventRequestMapper {
    @Mapping(target = "event", source = "eventRequest.event.id")
    @Mapping(target = "requester", source = "eventRequest.requester.id")
    ParticipationRequestDto toDto(EventRequest eventRequest);

    @Mapping(target = "event", source = "eventRequest.event.id")
    @Mapping(target = "requester", source = "eventRequest.requester.id")
    List<ParticipationRequestDto> toDto(List<EventRequest> eventRequest);
}
