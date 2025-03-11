package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;

    public List<Compilation> getCompilations(Boolean pinned, int from, int size) {
        log.debug("Получена подборка событий по параметрам: pinned - {} from - {} size - {}", pinned, from, size);
        if (pinned == null)
            return compilationRepository.findAll(PageRequest.of(from, size)).getContent();
        return compilationRepository.findAllByPinned(pinned, PageRequest.of(from, size)).getContent();
    }

    public Compilation getCompilation(long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Подборка с id %d не найдена", id))
        );
        log.debug("Получена подборка с id {}", id);
        return compilation;
    }

    @Transactional
    public Compilation addCompilation(Compilation compilation) {
        log.debug("Добавлена новая подборка {}", compilation);
        return compilationRepository.save(compilation);
    }

    @Transactional
    public void deleteCompilation(long compId) {
        log.debug("Запрос (admin) на удаление подборки c id {}", compId);
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
        } else {
            throw new NotFoundException(String.format("Подборка с id %d не найдена", compId));
        }
    }

    @Transactional
    public Compilation updateCompilation(Compilation updatedComp) {
        Compilation compForUpdate = compilationRepository.findById(updatedComp.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Подборка с id %d не найдена", updatedComp.getId()))
        );
        if (updatedComp.getPinned() != null) {
            compForUpdate.setPinned(updatedComp.getPinned());
        }
        if (updatedComp.getTitle() != null) {
            compForUpdate.setTitle(updatedComp.getTitle());
        }
        compForUpdate.setEvents(updatedComp.getEvents());
        return compilationRepository.save(compForUpdate);
    }
}
