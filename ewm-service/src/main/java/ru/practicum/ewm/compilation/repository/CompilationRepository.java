package ru.practicum.ewm.compilation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

    @Query("SELECT c.events FROM Compilation AS c WHERE c.id = :compId ")
    List<Event> getEventsByCompilation(@Param("compId") Long compilationId);

    @Query("SELECT c.events FROM Compilation AS c WHERE c.id IN :compIds ")
    List<Event> getEventsByCompilations(@Param("compIds") List<Long> compilationIds);
}
