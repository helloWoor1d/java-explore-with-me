package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.dto.enums.EventAdminStateAction;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.validation.ValidEventDate;

import java.time.LocalDateTime;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMAT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class UpdateEventAdminRequest {
    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    @PositiveOrZero
    private Integer category;

    @Size(min = 20, max = 7000)
    private String description;

    @ValidEventDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventAdminStateAction stateAction;
}
