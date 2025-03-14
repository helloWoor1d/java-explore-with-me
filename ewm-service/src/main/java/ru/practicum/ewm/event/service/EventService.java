package ru.practicum.ewm.event.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.dto.enums.EventAdminStateAction;
import ru.practicum.ewm.event.dto.enums.EventUserStateAction;
import ru.practicum.ewm.event.filter.admin.EventAdminFilter;
import ru.practicum.ewm.event.filter.admin.EventSpecAdmin;
import ru.practicum.ewm.event.filter.user.EventPublicUserFilter;
import ru.practicum.ewm.event.filter.user.EventSort;
import ru.practicum.ewm.event.filter.user.EventSpecPublicUser;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventFullWithViews;
import ru.practicum.ewm.event.model.EventShort;
import ru.practicum.ewm.event.model.EventShortWithViews;
import ru.practicum.ewm.event.model.enums.EventState;
import ru.practicum.ewm.event.model.mapper.EventModelMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.eventrequest.model.EventRequest;
import ru.practicum.ewm.eventrequest.service.EventRequestService;
import ru.practicum.ewm.exception.BadOperationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.sort.EventDateComparator;
import ru.practicum.ewm.sort.EventViewsComparator;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.stats.model.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMATTER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private static final String APP_NAME = "ewm-main-service";

    private final UserService userService;
    private final EventRequestService requestService;
    private final CommentService commentService;
    private final EventModelMapper modelMapper;

    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Transactional
    public Event addEvent(Event event, long userId) {
        log.debug("Добавлено событие event: {}", event);
        return eventRepository.save(event);
    }

    public List<EventShort> getUserEvents(long userId, int from, int size) {
        userService.getUser(userId);
        log.debug("Получены события добавленные пользователем с id {}; from = {}; size {}", userId, from, size);
        Pageable pageable = PageRequest.of(from, size);
        return eventRepository.getAllByInitiatorId(userId, pageable);
    }

    public EventFullWithViews getEvent(long userId, long eventId) {
        userService.getUser(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с указанным id %d не найдено.", eventId)));
        log.debug("Получено событие по id {}, пользователем с id {}", eventId, userId);
        return setViewsForEventsFull(List.of(event), false).getFirst();
    }

    public EventFullWithViews getEventPublic(long eventId, String uri, String userIp) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие недоступно.");
        }
        sendStats(uri, userIp);
        return setViewsForEventsFull(List.of(event), true).getFirst();
    }

    private List<EventFullWithViews> setViewsForEventsFull(List<Event> events, boolean unique) {
        List<Long> ids = events.stream().map(Event::getId).toList();

        List<EventRequest> requests = requestService.getConfirmedRequests(ids);
        List<ViewStatsDto> views = getEventsViews(ids, unique);
        List<Comment> comments = commentService.getComments(ids);
        List<EventFullWithViews> eventsFull = modelMapper.toEventFullWithViews(events);

        Map<Long, Long> countRequest = requests.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEvent().getId(), Collectors.counting()));

        Map<Long, Long> countComments = comments.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getEvent().getId(), Collectors.counting()));

        eventsFull.forEach(e -> {
            e.setConfirmedRequests(
                    countRequest.getOrDefault(e.getId(), 0L));
            e.setComments(countComments.getOrDefault(e.getId(), 0L));
        });

        Map<String, Long> urisViews = views.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
        eventsFull.forEach(e -> {
            String uri = "/events/" + e.getId();
            e.setViews(urisViews.getOrDefault(uri, 0L));
        });
        return eventsFull;
    }

    public List<EventShortWithViews> getEventsPublic(EventPublicUserFilter filter, int from, int size,
                                                     String uri, String userIp) {
        Specification<Event> spec = EventSpecPublicUser.filterBy(filter);
        Page<Event> events = eventRepository.findAll(spec, PageRequest.of(from, size));
        log.debug("Запрос на выборку событий (user) по параметра вильтрации {}", filter);

        sendStats(uri, userIp);
        return setViewsForEventsShort(events.getContent(), true);
    }

    private List<EventShortWithViews> setViewsForEventsShort(List<Event> events, boolean unique) {
        List<Long> ids = events.stream().map(Event::getId).toList();

        List<EventRequest> requests = requestService.getConfirmedRequests(ids);
        List<ViewStatsDto> views = getEventsViews(ids, unique);
        List<EventShortWithViews> eventShorts = modelMapper.toEventShortWithViews(events);

        Map<Long, Long> countRequest = requests.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEvent().getId(), Collectors.counting()));
        eventShorts.forEach(e -> e.setConfirmedRequests(
                countRequest.getOrDefault(e.getId(), 0L)));

        Map<String, Long> urisViews = views.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
        eventShorts.forEach(e -> {
            String uri = "/events/" + e.getId();
            e.setViews(urisViews.getOrDefault(uri, 0L));
        });
        return eventShorts;
    }

    @Transactional
    public Event updateEventByUser(long userId, long eventId, UpdateEventUserRequest updatedRequest) {
        log.debug("Получен запрос на изменение события userId = {}, eventId = {}, parameters for update = {}", userId, eventId, updatedRequest);
        userService.getUser(userId);
        Event eventForUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с указанным id %d не найдено.", eventId)));

        if (eventForUpdate.getState() == EventState.PUBLISHED) {
            throw new BadOperationException("Изменить можно только неопубликованное событие.");
        }
        modelMapper.updateEvent(updatedRequest, eventForUpdate);

        if (updatedRequest.getStateAction() == EventUserStateAction.CANCEL_REVIEW) {
            eventForUpdate.setState(EventState.CANCELED);
        } else {
            eventForUpdate.setState(EventState.PENDING);
        }
        return eventRepository.save(eventForUpdate);
    }

    @Transactional
    public Event updateEventByAdmin(long eventId, UpdateEventAdminRequest updatedRequest) {
        log.debug("Изменение события (ADMIN) parameters for update = {}", updatedRequest);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с указанным id %d не найдено.", eventId)));

        if (event.getEventDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new BadOperationException("Нельзя изменить событие за час до его начала");
        }

        if (updatedRequest.getStateAction() == EventAdminStateAction.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new BadOperationException("Событие можно отклонить, только если оно еще не опубликовано");
            }
            event.setState(EventState.REJECTED);
        } else {
            if (event.getState() != EventState.PENDING) {
                throw new BadOperationException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        modelMapper.updateEvent(updatedRequest, event);
        return eventRepository.save(event);
    }

    public List<EventFullWithViews> getEventsByAdmin(EventAdminFilter filter, int from, int size) {
        Specification<Event> spec = EventSpecAdmin.filterBy(filter);
        log.debug("Запрос на выборку событий (админ) по параметрам фильтрации {}", filter);
        Page<Event> events = eventRepository.findAll(spec, PageRequest.of(from, size));
        return setViewsForEventsFull(events.getContent(), false);
    }

    public List<EventShortWithViews> sortEvents(List<EventShortWithViews> events, EventSort sort) {
        if (sort == null) return events;
        switch (sort) {
            case EVENT_DATE -> events.sort(new EventDateComparator());
            case VIEWS -> events.sort(new EventViewsComparator());
        }
        return events;
    }

    @Nullable
    public List<Event> getEventsByIds(Set<Long> ids) {
        log.debug("Получен список событий по id [{}]", ids);
        if (ids == null) return null;
        else if (ids.isEmpty()) return new ArrayList<>();
        List<Event> events = eventRepository.findAllById(ids);
        return events.isEmpty() ? new ArrayList<>() : events;
    }

    public void sendStats(String uri, String userIp) {
        log.debug("Отправлена статистика посещения uri {}, userIp {}", uri, userIp);
        statsClient.hit(APP_NAME, uri, userIp, LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    private List<ViewStatsDto> getEventsViews(List<Long> ids, boolean unique) {
        List<String> eventUris = ids.stream().map(id -> "/events/" + id).toList();

        List<ViewStatsDto> views = statsClient.getStats(LocalDateTime.now().minusDays(30).format(DATE_TIME_FORMATTER),
                LocalDateTime.now().format(DATE_TIME_FORMATTER), eventUris, unique);
        log.debug("Получена информация о просмотрах для списка событий ids = {}, views = {}", ids, views);
        return views;
    }
}
