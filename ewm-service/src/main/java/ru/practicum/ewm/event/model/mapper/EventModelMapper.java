package ru.practicum.ewm.event.model.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventShortWithViews;
import ru.practicum.ewm.event.model.EventFullWithViews;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryService.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventModelMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventForUpdate.category", source = "adminRequest.category")
    void updateEvent(UpdateEventAdminRequest adminRequest, @MappingTarget Event eventForUpdate);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventForUpdate.category", source = "userRequest.category")
    void updateEvent(UpdateEventUserRequest userRequest, @MappingTarget Event eventForUpdate);

    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    List<EventShortWithViews> toEventShortWithViews(List<Event> event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    List<EventFullWithViews> toEventFullWithViews(List<Event> event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    EventFullWithViews toEventFullWithViews(Event event);
}
