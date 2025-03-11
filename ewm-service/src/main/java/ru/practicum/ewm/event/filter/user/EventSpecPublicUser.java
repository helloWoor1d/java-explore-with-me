package ru.practicum.ewm.event.filter.user;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.eventrequest.model.EventRequest;

import java.time.LocalDateTime;
import java.util.List;

public class EventSpecPublicUser {
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String ANNOTATION = "annotation";
    public static final String STATUS = "status";
    public static final String CATEGORY = "category";
    public static final String EVENT_DATE = "eventDate";
    public static final String PAID = "paid";
    public static final String PARTICIPANT_LIMIT = "participantLimit";

    private EventSpecPublicUser() {
    }

    public static Specification<Event> filterBy(EventPublicUserFilter filter) {
        return Specification
                .where(hasText(filter.getText()))
                .and(inCategories(filter.getCategories()))
                .and(hasPaid(filter.getPaid()))
                .and(withRangeStart(filter.getRangeStart()))
                .and(withRangeEnd(filter.getRangeEnd()))
                .and(hasAvailability(filter.getOnlyAvailable()));
    }

    private static Specification<Event> hasText(String text) {
        return ((root, query, criteriaBuilder) -> {
            if (text == null) {
                return criteriaBuilder.conjunction();
            } else {
                String pattern = "%" + text + "%";
                Predicate titlePrd = criteriaBuilder.like(root.get(TITLE), pattern);
                Predicate annotationPrd = criteriaBuilder.like(root.get(ANNOTATION), pattern);
                Predicate descriptionPrd = criteriaBuilder.like(root.get(DESCRIPTION), pattern);
                return criteriaBuilder.or(titlePrd, descriptionPrd, annotationPrd);
            }
        });
    }

    private static Specification<Event> inCategories(List<Long> categories) {
        return ((root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            } else {
                return root.get(CATEGORY).get("id").in(categories);
            }
        });
    }

    private static Specification<Event> hasPaid(Boolean paid) {
        return (((root, query, criteriaBuilder) -> {
            if (paid == null) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.equal(root.get(PAID), paid);
            }
        }));
    }

    public static Specification<Event> withRangeStart(LocalDateTime start) {
        return (((root, query, criteriaBuilder) -> {
            if (start == null) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(EVENT_DATE), start);
            }
        }));
    }

    public static Specification<Event> withRangeEnd(LocalDateTime end) {
        return (((root, query, criteriaBuilder) -> {
            if (end == null) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get(EVENT_DATE), end);
            }
        }));
    }

    public static Specification<Event> hasAvailability(Boolean onlyAvailable) {
        return (((root, query, criteriaBuilder) -> {
            if (onlyAvailable == null || !onlyAvailable) {
                return criteriaBuilder.conjunction();
            }

            // подзапрос для поиска количества подтвержденных заявок на событие
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<EventRequest> eventRequestRoot = subquery.from(EventRequest.class);
            subquery.select(criteriaBuilder.count(eventRequestRoot.get("event").get("id")));
            subquery.where(
                    criteriaBuilder.equal(eventRequestRoot.get("event").get("id"), root.get("id")),
                    criteriaBuilder.equal(eventRequestRoot.get(STATUS), "CONFIRMED")
            );

            Predicate unlimitedParticipant = criteriaBuilder.equal(root.get(PARTICIPANT_LIMIT), 0);

            return criteriaBuilder.or(
                    unlimitedParticipant,
                    criteriaBuilder.lessThan(subquery, root.get(PARTICIPANT_LIMIT)));
        }));
    }
}
