package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.dto.mapper.EventDtoMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventFullWithViews;
import ru.practicum.ewm.event.model.EventShort;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.eventrequest.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.eventrequest.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.eventrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.eventrequest.dto.mapper.EventRequestMapper;
import ru.practicum.ewm.eventrequest.model.EventRequest;
import ru.practicum.ewm.eventrequest.model.enums.EventRequestStatus;
import ru.practicum.ewm.eventrequest.service.EventRequestService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventDtoMapper dtoMapper;
    private final EventRequestMapper requestMapper;
    private final CommentMapper commentMapper;

    private final EventService eventService;
    private final EventRequestService requestService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto newEventDto) {
        log.debug("controller: добавлено событие {} пользователем {}", newEventDto, userId);
        Event event = dtoMapper.fromDto(newEventDto, userId);
        return dtoMapper.toFullDto(
                eventService.addEvent(event, userId));
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.debug("controller: запрос на выборку событий пользователя {}", userId);
        List<EventShort> events = eventService.getUserEvents(userId, from, size);
        return dtoMapper.toEventShortsDto(events);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.debug("controller: получена полная информация о событии {} пользователем {}", eventId, userId);
        EventFullWithViews event = eventService.getEvent(userId, eventId);
        return dtoMapper.toEventFullDto(event);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventRequest) {
        log.debug("controller: изменение события (USER)");
        return dtoMapper.toFullDto(
                eventService.updateEventByUser(userId, eventId, updateEventRequest));
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getInitiatorRequests(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        log.debug("controller: получены запросы для события {} пользователя {}", eventId, userId);
        List<EventRequest> requests = requestService.getInitiatorEventRequests(userId, eventId);
        return requestMapper.toDto(requests);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.debug("controller: изменение статуса запросов {} события {} пользователем {}", updateRequest, eventId, userId);
        List<EventRequest> requests = requestService.updateRequestsStatus(userId, eventId,
                updateRequest.getRequestIds(), updateRequest.getStatus());

        List<EventRequest> confirmedRequests = new ArrayList<>();
        List<EventRequest> rejectedRequests = new ArrayList<>();
        for (EventRequest request : requests) {
            if (request.getStatus() == EventRequestStatus.REJECTED) {
                rejectedRequests.add(request);
            } else {
                confirmedRequests.add(request);
            }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestMapper.toDto(confirmedRequests))
                .rejectedRequests(requestMapper.toDto(rejectedRequests))
                .build();
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto addComment(@Valid @RequestBody CommentDto comment,
                                     @PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.debug("controller: добавление комментария к событию {} пользователем {}; comment: {}", eventId, userId, comment);
        return commentMapper.toCommentFullDto(
                commentService.addComment(comment, userId, eventId));
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public CommentFullDto updateComment(@Valid @RequestBody CommentDto comment,
                                        @PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @PathVariable Long commentId) {
        log.debug("controller: изменение комментария к событию {} пользователем {}; comment: {}", eventId, userId, comment);
        return commentMapper.toCommentFullDto(
                commentService.updateComment(comment, userId, eventId, commentId));
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.debug("controller: удаление комментария {} (USER) к событию {}", commentId, eventId);
        commentService.deleteCommentByUser(userId, eventId, commentId);
    }
}
