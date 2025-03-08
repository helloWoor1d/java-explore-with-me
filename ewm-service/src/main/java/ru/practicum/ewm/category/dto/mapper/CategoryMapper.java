package ru.practicum.ewm.category.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category fromDto(NewCategoryDto dto);

    @Mapping(target = "id", source = "id")
    Category fromDto(NewCategoryDto dto, Long id);

    CategoryDto toDto(Category category);

    List<CategoryDto> toDto(Page<Category> categories);
}
