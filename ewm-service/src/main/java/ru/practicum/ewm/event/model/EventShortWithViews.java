package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventShortWithViews {
    private Long id;
    private String title;
    private CategoryDto category;
    private String annotation;
    private UserShortDto initiator;
    private LocalDateTime eventDate;
    private Boolean paid;
    private Long confirmedRequests;
    private Long views;
}
