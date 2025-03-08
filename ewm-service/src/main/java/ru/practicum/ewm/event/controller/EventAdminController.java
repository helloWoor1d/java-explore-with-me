package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.mapper.EventDtoMapper;
import ru.practicum.ewm.event.filter.admin.EventAdminFilter;
import ru.practicum.ewm.event.model.EventFullWithViews;
import ru.practicum.ewm.event.model.enums.EventState;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {
    private final EventService eventService;
    private final EventDtoMapper dtoMapper;

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<EventState> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10") Integer size) {
        log.debug("controller: запрос на поиск событий по фильтрам (ADMIN)");
        EventAdminFilter adminFilter = EventAdminFilter.builder()
                .initiators(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        List<EventFullWithViews> events = eventService.getEventsByAdmin(adminFilter, from, size);
        return dtoMapper.toEventFullDto(events);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable("eventId") Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        log.debug("controller: запрос на изменение события (ADMIN)");
        return dtoMapper.toFullDto(
                eventService.updateEventByAdmin(eventId, updateRequest));
    }
}
