package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

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
