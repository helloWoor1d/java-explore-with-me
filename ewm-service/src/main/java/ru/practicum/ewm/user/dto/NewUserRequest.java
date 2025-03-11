package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewUserRequest {
    @NotBlank(message = "email address cannot be blank")
    @Email(regexp = ".+[@].+[\\.].+", message = "email is not valid")
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank(message = "user's name cannot be blank")
    @Size(min = 2, max = 250)
    private String name;
}
