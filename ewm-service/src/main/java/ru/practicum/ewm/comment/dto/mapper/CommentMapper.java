package ru.practicum.ewm.comment.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "author", source = "comment.author.id")
    @Mapping(target = "event", source = "comment.event.id")
    CommentFullDto toCommentFullDto(Comment comment);

    List<CommentFullDto> toCommentFullDto(List<Comment> comments);
}
