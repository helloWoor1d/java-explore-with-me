package ru.practicum.ewm.eventrequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.eventrequest.model.EventRequest;
import ru.practicum.ewm.eventrequest.model.enums.EventRequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByRequesterId(Long requesterId);

    @Query("SELECT r " +
            "FROM EventRequest r " +
            "JOIN Event e ON r.event.id = e.id " +
            "WHERE e.id = :eventId " +
            "AND e.initiator.id = :initiatorId " +
            "ORDER BY r.created DESC ")
    List<EventRequest> getInitiatorEventRequests(@Param("initiatorId") long initiatorId,
                                                 @Param("eventId") long eventId);

    List<EventRequest> findAllByIdInAndStatus(List<Long> ids, EventRequestStatus status);

    Long countAllByEventIdAndStatus(Long eventId, EventRequestStatus status);

    List<EventRequest> getAllByEventIdInAndStatus(List<Long> ids, EventRequestStatus status);

    Optional<EventRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);
}
