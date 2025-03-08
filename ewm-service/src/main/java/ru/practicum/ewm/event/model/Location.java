package ru.practicum.ewm.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Embeddable
public class Location {
    @Column(name = "latitude")
    private Float lat;

    @Column(name = "longitude")
    private Float lon;
}
