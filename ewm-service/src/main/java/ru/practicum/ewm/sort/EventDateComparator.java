package ru.practicum.ewm.sort;

import ru.practicum.ewm.event.model.EventShortWithViews;

import java.util.Comparator;

public class EventDateComparator implements Comparator<EventShortWithViews> {
    @Override
    public int compare(EventShortWithViews o1, EventShortWithViews o2) {
        return o1.getEventDate().compareTo(o2.getEventDate());
    }
}
