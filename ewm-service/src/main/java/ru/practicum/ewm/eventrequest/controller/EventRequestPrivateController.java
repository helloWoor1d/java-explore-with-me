package ru.practicum.ewm.eventrequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.eventrequest.dto.ParticipationRequestDto;
import ru.practicum.ewm.eventrequest.model.EventRequest;
import ru.practicum.ewm.eventrequest.dto.mapper.EventRequestMapper;
import ru.practicum.ewm.eventrequest.service.EventRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class EventRequestPrivateController {
    private final EventRequestService requestService;
    private final EventRequestMapper requestMapper;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.debug("КОНТРОЛЛЕР: Получены запросы пользователя {}", userId);
        List<EventRequest> requests = requestService.getUserRequests(userId);
        return requestMapper.toDto(requests);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable Long userId,
                                                     @RequestParam Long eventId) {
        log.debug("controller: Создан запрос на участие в событии {} пользователем {}", eventId, userId);
        EventRequest request = requestService.addRequest(userId, eventId);
        return requestMapper.toDto(request);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        log.debug("controller: Запрос {} отменен пользователем {}", requestId, userId);
        EventRequest request = requestService.cancelRequest(userId, requestId);
        return requestMapper.toDto(request);
    }
}
