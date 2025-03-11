package ru.practicum.ewm.event.filter.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.event.model.enums.EventState.PUBLISHED;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class EventPublicUserFilter {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;

    @Builder.Default
    private EventState state = PUBLISHED;
}
