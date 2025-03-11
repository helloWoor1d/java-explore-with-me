package ru.practicum.ewm.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.ewm.exception.BadOperationException;

import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<ValidEventDate, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate != null && !eventDate.isAfter(LocalDateTime.now().plusHours(2))) {
            throw new BadOperationException("Дата и время созданного события не могут быть раньше 2-х часов от текущего времени");
        }
        return true;
    }
}
