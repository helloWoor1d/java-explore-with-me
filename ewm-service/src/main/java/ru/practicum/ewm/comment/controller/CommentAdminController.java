package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.debug("controller: удаление комментария {} к событию {} (ADMIN)", commentId, eventId);
        commentService.deleteCommentByAdmin(eventId, commentId);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentFullDto> getComments(@PathVariable("eventId") Long id,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "20") Integer size) {
        log.debug("controller: получение комментариев (ADMIN) к событию {}", id);
        return commentMapper.toCommentFullDto(
                commentService.getCommentsByAdmin(id, from, size));
    }
}
