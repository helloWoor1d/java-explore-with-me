package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.enums.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMAT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventFullDto {
    @NotNull
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private CategoryDto category;

    @NotBlank
    private String annotation;

    private String description;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    private EventState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;

    @PositiveOrZero
    private Integer participantLimit;

    @PositiveOrZero
    private Long confirmedRequests;

    private Boolean requestModeration;

    @PositiveOrZero
    private Long views;

    @PositiveOrZero
    private Long comments;
}
