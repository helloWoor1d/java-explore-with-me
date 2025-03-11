package ru.practicum.ewm.eventrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.eventrequest.model.enums.EventRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMAT;

@Data
@Builder
public class ParticipationRequestDto {
    @NotNull
    private Integer id;

    @NotNull
    private Integer event;

    @NotNull
    private Integer requester;

    @NotNull
    private EventRequestStatus status;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT)
    private LocalDateTime created;
}
