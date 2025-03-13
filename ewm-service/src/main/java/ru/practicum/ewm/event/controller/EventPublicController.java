package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.mapper.EventDtoMapper;
import ru.practicum.ewm.event.filter.user.EventPublicUserFilter;
import ru.practicum.ewm.event.filter.user.EventSort;
import ru.practicum.ewm.event.model.EventFullWithViews;
import ru.practicum.ewm.event.model.EventShortWithViews;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMATTER;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventService eventService;
    private final CommentService commentService;

    private final EventDtoMapper dtoMapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public List<EventShortDto> getEventsByFilter(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) EventSort sort,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 final HttpServletRequest request) {
        LocalDateTime start = rangeStart == null ? null : LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        LocalDateTime end = rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        if (start != null && end != null && start.isAfter(end))
            throw new ValidationException("Date start must be before end");

        EventPublicUserFilter filter = EventPublicUserFilter.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(start)
                .rangeEnd(end)
                .onlyAvailable(onlyAvailable)
                .sort(sort).build();
        log.debug("controller: запрос на выборку событий по фильтру (USER)");

        List<EventShortWithViews> events = eventService.getEventsPublic(filter, from, size, request.getRequestURI(), request.getRemoteAddr());
        events = eventService.sortEvents(events, sort);

        return dtoMapper.toEventShortDto(events);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable("id") Long id, final HttpServletRequest request) {
        log.debug("controller: получено событие (USER) с id {}", id);
        EventFullWithViews event = eventService.getEventPublic(id, request.getRequestURI(), request.getRemoteAddr());
        return dtoMapper.toEventFullDto(event);
    }

    @GetMapping("/{id}/comments")
    public List<CommentFullDto> getComments(@PathVariable("id") Long id,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "20") Integer size) {
        log.debug("controller: получение комментариев (USER) к событию {}", id);
        return commentMapper.toCommentFullDto(
                commentService.getCommentsByUser(id, from, size));
    }
}
