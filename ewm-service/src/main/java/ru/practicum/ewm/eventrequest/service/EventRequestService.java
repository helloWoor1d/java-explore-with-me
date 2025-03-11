package ru.practicum.ewm.eventrequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.enums.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.eventrequest.model.EventRequest;
import ru.practicum.ewm.eventrequest.model.enums.EventRequestStatus;
import ru.practicum.ewm.eventrequest.repository.EventRequestRepository;
import ru.practicum.ewm.exception.BadOperationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventRequestService {
    private final UserService userService;

    private final EventRequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Transactional
    public EventRequest addRequest(long userId, long eventId) {
        User user = userService.getUser(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с указанным id %d не найдено.", eventId)));

        if (event.getInitiator().getId() == userId)
            throw new BadOperationException("Инициатор события не может подать запрос на участие в своем событии");
        if (event.getState() != (EventState.PUBLISHED))
            throw new BadOperationException("Нельзя подать заявку на участие в еще неопубликованное событие");
        if (event.getParticipantLimit() <= getCountConfirmedRequests(eventId) && event.getParticipantLimit() != 0)
            throw new BadOperationException("Достигнут лимит участников события");

        EventRequestStatus requestStatus;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestStatus = EventRequestStatus.CONFIRMED;
        } else requestStatus = EventRequestStatus.PENDING;

        requestRepository.findByEventIdAndRequesterId(eventId, userId)
                .ifPresent(r -> {
                    throw new BadOperationException("Запрос на участие в этом событии уже существует");
                });

        EventRequest request = EventRequest.builder()
                .event(event)
                .requester(user)
                .status(requestStatus)
                .created(LocalDateTime.now())
                .build();
        log.debug("Добавлен запрос на участие в событии request = {}", request);
        return requestRepository.save(request);
    }

    @Transactional
    public EventRequest cancelRequest(long userId, long requestId) {
        userService.getUser(userId);
        EventRequest request = getUserRequest(userId, requestId);
        request.setStatus(EventRequestStatus.CANCELED);

        log.debug("Отмена запроса на участие в событии; userId = {}, requestId = {}", userId, requestId);
        return request;
    }

    public EventRequest getUserRequest(long userId, long requestId) {
        EventRequest request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос на участие в событии с id %d не найден", requestId)));

        if (request.getRequester().getId() != userId)
            throw new BadOperationException(String.format("Запрос на участие с id %d не принадлежит пользователю с id %d", requestId, userId));

        log.debug("Получен запрос на участие в событии с id {}, пользователем с id {}", requestId, userId);
        return request;
    }

    public List<EventRequest> getUserRequests(long userId) {
        userService.getUser(userId);
        log.debug("Получена информация о заявках пользователя id {} на участи в событиях", userId);
        return requestRepository.findAllByRequesterId(userId);
    }

    public List<EventRequest> getInitiatorEventRequests(long initiatorId, long eventId) {
        userService.getUser(initiatorId);
        List<EventRequest> requests = requestRepository.getInitiatorEventRequests(initiatorId, eventId);
        log.debug("Получение информации о запросах на участие в событии id {} пользователя с id {}", eventId, initiatorId);
        return requests;
    }

    @Transactional
    public List<EventRequest> updateRequestsStatus(long userId, long eventId,
                                                   List<Long> requestsIds, EventRequestStatus status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с указанным id %d не найдено.", eventId)));

        if (event.getInitiator().getId() != userId)
            throw new NotFoundException(String.format("Событие с id = %d пользователя с id = %d не найдено", eventId, userId));

        List<EventRequest> requests = getPendingRequests(requestsIds);
        if (requests.size() < requestsIds.size())
            throw new BadOperationException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");

        if (status == EventRequestStatus.REJECTED) {
            rejectRequests(requests);
            requests = requestRepository.saveAll(requests);
            log.debug("Статус запросов {} изменен на {}", requestsIds, status);
            return requests;
        } else if (status == EventRequestStatus.CONFIRMED) {
            int limit = event.getParticipantLimit();
            long confirmed = getCountConfirmedRequests(eventId);

            if (!event.getRequestModeration() || limit == 0) return Collections.emptyList();
            if (limit == confirmed)
                throw new BadOperationException("Достигнут лимит заявок на данное событие");

            confirmRequests(requests, limit, confirmed);
            requests = requestRepository.saveAll(requests);
            log.debug("Статус запросов {} изменен", requestsIds);
            return requests;
        }
        throw new BadOperationException("Пользователь может только подтердить или отклонить заявку на участие в своем событии");
    }

    public Long getCountConfirmedRequests(long eventId) {
        Long count = requestRepository.countAllByEventIdAndStatus(eventId, EventRequestStatus.CONFIRMED);
        log.debug("Получено количество одобренных запросов на участие в событии с id {}, count - {}", eventId, count);
        return count;
    }

    public List<EventRequest> getConfirmedRequests(List<Long> eventIds) {
        List<EventRequest> requests = requestRepository.getAllByEventIdInAndStatus(eventIds, EventRequestStatus.CONFIRMED);
        log.debug("Получена информация по одобренным запросам на участие в событиях: {}, requests = {}", eventIds, requests);
        return requests;
    }

    private void rejectRequests(List<EventRequest> requests) {
        for (EventRequest request : requests) {
            request.setStatus(EventRequestStatus.REJECTED);
        }
    }

    private void confirmRequests(List<EventRequest> requests, int limit, long confirmed) {
        long freeSpaces = limit - confirmed;
        for (EventRequest request : requests) {
            if (freeSpaces != 0) {
                request.setStatus(EventRequestStatus.CONFIRMED);
                freeSpaces--;
                confirmed++;
            } else {
                request.setStatus(EventRequestStatus.REJECTED);
            }
        }
    }

    private List<EventRequest> getPendingRequests(List<Long> requestsIds) {
        return requestRepository.findAllByIdInAndStatus(requestsIds, EventRequestStatus.PENDING);
    }
}
