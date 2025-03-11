package ru.practicum.stats.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.debug("Обработано исключение MethodArgumentNotValidException");
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Запрос составлен некорректно.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }


    @ExceptionHandler(jakarta.validation.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final ValidationException ex) {
        log.debug("Обработано исключение ValidationException");
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Запрос составлен некорректно.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
