package ru.practicum.ewm.event.filter.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.model.enums.EventState;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class EventAdminFilter {
    private List<Long> initiators;
    private List<EventState> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
}
