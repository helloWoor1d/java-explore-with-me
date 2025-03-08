package ru.practicum.ewm.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException ex) {
        log.debug("Обработано исключение DataIntegrityViolationException");
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Нарушено ограничение целостности данных")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ValidationException.class)
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

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException ex) {
        log.debug("Обработано исключение NotFoundException");
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason("Запрашиваемый ресурс не найден.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(BadOperationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleBadOperationException(final BadOperationException ex) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Событие не удовлетворяет правилам создания.")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
