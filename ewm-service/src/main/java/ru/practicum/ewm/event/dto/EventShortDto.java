package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMAT;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class EventShortDto {
    @NotNull
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private CategoryDto category;

    @NotBlank
    private String annotation;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @NotNull
    private Boolean paid;

    private Long confirmedRequests;

    private Long views;
}
