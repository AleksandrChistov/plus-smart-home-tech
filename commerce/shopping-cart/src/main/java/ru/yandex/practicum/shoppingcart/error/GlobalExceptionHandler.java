package ru.yandex.practicum.shoppingcart.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.yandex.practicum.api.shared.error.NotFoundException;
import ru.yandex.practicum.api.warehouse.error.InsufficientStockError;
import ru.yandex.practicum.api.warehouse.error.ServiceUnavailableException;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(final DataIntegrityViolationException ex) {
        log.warn("409 Conflict: {}", ex.getMessage());
        return getResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidInput(MethodArgumentNotValidException ex) {
        log.warn("400 {}", ex.getMessage());

        String message = Optional
                .ofNullable(ex.getBindingResult().getAllErrors().getFirst())
                .map(ObjectError::getDefaultMessage)
                .orElse(ex.getMessage());

        return getResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(InsufficientStockError.class)
    public ResponseEntity<ApiError> handleInsufficientStockException(final InsufficientStockError ex) {
        log.warn("400 {}", ex.getMessage());
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.insufficientItems);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException ex) {
        log.warn("404 {}", ex.getMessage());
        return getResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleMethodValidationException(final HandlerMethodValidationException ex) {
        log.warn("422 {}", ex.getMessage());
        return getResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), null);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailableException(final ServiceUnavailableException ex) {
        log.error("503 {}", ex.getMessage(), ex);
        return getResponseEntity(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(final Exception ex) {
        log.error("500 {}", ex.getMessage(), ex);
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    private ResponseEntity<ApiError> getResponseEntity(HttpStatus httpStatus, String message, Object info) {
        ApiError apiError = new ApiError(httpStatus, message, info);
        return new ResponseEntity<>(apiError, httpStatus);
    }

}
