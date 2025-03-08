package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.enums.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventFullWithViews {
    private Long id;
    private String title;
    private CategoryDto category;
    private String annotation;
    private String description;
    private UserShortDto initiator;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private EventState state;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Integer participantLimit;
    private Boolean requestModeration;
    private Long confirmedRequests;
    private Long views;
}
