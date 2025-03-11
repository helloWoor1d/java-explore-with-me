package ru.practicum.ewm.eventrequest.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.eventrequest.model.enums.EventRequestStatus;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    @NotEmpty
    private List<Long> requestIds;

    private EventRequestStatus status;
}
