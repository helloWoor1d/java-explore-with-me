package ru.practicum.ewm.event.model;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.UserShort;

import java.time.LocalDateTime;

public interface EventShort {
    Long getId();

    String getTitle();

    Category getCategory();

    String getAnnotation();

    UserShort getInitiator();

    LocalDateTime getEventDate();

    Boolean getPaid();
}
