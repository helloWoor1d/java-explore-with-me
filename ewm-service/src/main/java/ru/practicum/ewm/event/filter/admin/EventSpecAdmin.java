package ru.practicum.ewm.event.filter.admin;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.model.DateTimeFormat.DATE_TIME_FORMATTER;

public class EventSpecAdmin {
    public static final String INITIATOR = "initiator";
    public static final String STATE = "state";
    public static final String CATEGORY = "category";
    public static final String EVENT_DATE = "eventDate";

    private EventSpecAdmin() {
    }

    public static Specification<Event> filterBy(EventAdminFilter filter) {
        return Specification
                .where(hasInitiators(filter.getInitiators()))
                .and(hasStates(filter.getStates()))
                .and(hasCategories(filter.getCategories()))
                .and(withRangeStart(filter.getRangeStart()))
                .and(withRangeEnd(filter.getRangeEnd()));
    }

    private static Specification<Event> hasInitiators(List<Long> initiatorIds) {
        return ((root, query, criteriaBuilder) -> {
            if (initiatorIds == null || initiatorIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                return root.get(INITIATOR).get("id").in(initiatorIds);
            }
        });
    }

    private static Specification<Event> hasStates(List<EventState> states) {
        return (((root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                return root.get(STATE).in(states);
            }
        }));
    }

    private static Specification<Event> hasCategories(List<Long> categoryIds) {
        return (((root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                return root.get(CATEGORY).get("id").in(categoryIds);
            }
        }));
    }

    private static Specification<Event> withRangeStart(String startStr) {
        return (((root, query, criteriaBuilder) -> {
            if (startStr == null || startStr.isBlank()) {
                return criteriaBuilder.conjunction();
            } else {
                LocalDateTime start = LocalDateTime.parse(startStr, DATE_TIME_FORMATTER);
                return criteriaBuilder.greaterThanOrEqualTo(root.get(EVENT_DATE), start);
            }
        }));
    }

    private static Specification<Event> withRangeEnd(String endStr) {
        return (((root, query, criteriaBuilder) -> {
            if (endStr == null || endStr.isBlank()) {
                return criteriaBuilder.conjunction();
            } else {
                LocalDateTime end = LocalDateTime.parse(endStr, DATE_TIME_FORMATTER);
                return criteriaBuilder.lessThanOrEqualTo(root.get(EVENT_DATE), end);
            }
        }));
    }
}
