package ru.practicum.ewm.comment.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final UserService userService;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    private final EntityManager entityManager;

    public Comment addComment(CommentDto commentDto, long userId, long eventId) {
        User author = userService.getUser(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        Comment comm = Comment.builder()
                .author(author)
                .text(commentDto.getText())
                .event(event)
                .created(LocalDateTime.now())
                .build();

        Comment comment = commentRepository.save(comm);
        log.debug("Добавлен к событию {} пользователем {}", eventId, userId);
        return comment;
    }

    public Comment updateComment(CommentDto commentDto, long userId, long eventId, long commentId) {
        Comment commForUpdate = getComment(commentId);
        if (commForUpdate.getAuthor().getId() != userId || commForUpdate.getEvent().getId() != eventId) {
            throw new NotFoundException("Комментарий не найден");
        }
        commForUpdate.setText(commentDto.getText());
        Comment comment = commentRepository.save(commForUpdate);

        log.debug("Изменен комментарий к событию {} пользователем {}", eventId, userId);
        return comment;
    }

    public Comment getComment(long commentId) {
        log.debug("Получен комментарий по id {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (comment.getDeleted())
            throw new NotFoundException("Комментарий был удален администартором");
        return comment;
    }

    public void deleteCommentByUser(long userId, long eventId, long commentId) {
        Comment comment = getComment(commentId);
        if (comment.getAuthor().getId() != userId || comment.getEvent().getId() != eventId) {
            throw new NotFoundException("Комментарий не найден");
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentByAdmin(long eventId, long commentId) {
        Comment comment = getComment(commentId);
        if (comment.getEvent().getId() != eventId) {
            throw new NotFoundException("Комментарий не найден");
        }
        comment.setDeleted(true);
        commentRepository.save(comment);
        log.debug("Удален комментарий {} (ADMIN) к событию {}", commentId, eventId);
    }

    public List<Comment> getCommentsByUser(long eventId, int from, int size) {
        List<Comment> comments = commentRepository.findAllByEventIdAndDeletedIsFalse(eventId, PageRequest.of(from, size));
        log.debug("Получены комментарии события {} (USER)", eventId);
        return comments;
    }

    public List<Comment> getCommentsByAdmin(long eventId, int from, int size) {
        List<Comment> comments = commentRepository.findAllByEventIdAndDeletedIsTrue(eventId, PageRequest.of(from, size));
        log.debug("Получены комментарии события {} (ADMIN)", eventId);
        return comments;
    }

    public List<Comment> getComments(List<Long> eventsIds) {
        List<Comment> comments = commentRepository.findAllByEventIdInAndDeletedIsFalse(eventsIds);
        log.debug("Получены комментарии для событий с id [{}]", eventsIds);
        return comments;
    }
}
