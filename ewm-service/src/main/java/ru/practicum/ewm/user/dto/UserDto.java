package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    @NotNull
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String name;
}
