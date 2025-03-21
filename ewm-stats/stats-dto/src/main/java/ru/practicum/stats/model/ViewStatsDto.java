package ru.practicum.stats.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewStatsDto {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    private Long hits;
}
