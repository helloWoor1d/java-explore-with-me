package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        log.debug("Запрос на получение пользователей: ids={}, from={}, size={}", ids, from, size);
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(page).getContent();
        }
        return userRepository.getAllByIdIn(ids, page);
    }

    @Transactional
    public User createUser(User user) {
        log.debug("Запрос на создание пользователя {}", user);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Запрос на удаление пользователя с id {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id %d не найден", id)));
    }
}
