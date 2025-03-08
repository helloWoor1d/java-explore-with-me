package ru.practicum.ewm.category.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EntityManager entityManager;

    public Page<Category> getCategories(Integer from, Integer size) {
        log.debug("Получены категории from {}, size {}", from, size);
        Pageable page = PageRequest.of(from, size);
        return categoryRepository.findAll(page);
    }

    public Category getCategory(Long id) {
        log.debug("Получена категория по id {}", id);
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id %d не найдена", id)));
    }

    @Transactional
    public Category createCategory(Category category) {
        log.debug("Создание категории category: {}", category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long catId) {
        log.debug("Удаление категории по id {}", catId);
        categoryRepository.deleteById(catId);
    }

    @Transactional
    public Category updateCategory(Long id, Category category) {
        categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Категория с id %d не найдена", id)));
        log.debug("Изменение категории category: {}", category);
        return categoryRepository.save(category);
    }
}
