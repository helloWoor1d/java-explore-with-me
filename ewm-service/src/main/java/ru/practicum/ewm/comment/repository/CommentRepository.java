package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdAndDeletedIsFalse(Long eventId, PageRequest page);

    List<Comment> findAllByEventIdAndDeletedIsTrue(Long eventId, PageRequest page);

    List<Comment> findAllByEventIdInAndDeletedIsFalse(List<Long> eventIds);
}
