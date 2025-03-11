package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.dto.enums.EventUserStateAction;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.validation.ValidEventDate;

import java.time.LocalDateTime;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMAT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UpdateEventUserRequest {
    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    @PositiveOrZero
    private Integer category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    @ValidEventDate
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventUserStateAction stateAction;
}
