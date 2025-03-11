package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class NewCompilationDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @Builder.Default
    private Boolean pinned = false;

    private Set<Long> events;
}
