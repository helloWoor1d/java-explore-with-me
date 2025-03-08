package ru.practicum.ewm.sort;

import ru.practicum.ewm.event.model.EventShortWithViews;

import java.util.Comparator;

public class EventViewsComparator implements Comparator<EventShortWithViews> {
    @Override
    public int compare(EventShortWithViews o1, EventShortWithViews o2) {
        if (o1.getViews().equals(o2.getViews())) return 0;
        return o1.getViews() > (o2.getViews()) ? -1 : 1;
    }
}
