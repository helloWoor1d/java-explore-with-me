package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/{id}/comments")
    public List<CommentFullDto> getComments(@PathVariable("id") Long id,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "20") Integer size) {
        log.debug("controller: получение комментариев (USER) к событию {}", id);
        return commentMapper.toCommentFullDto(
                commentService.getCommentsByUser(id, from, size));
    }
}
