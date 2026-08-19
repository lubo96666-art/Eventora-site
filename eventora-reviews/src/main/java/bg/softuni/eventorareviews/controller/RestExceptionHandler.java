package bg.softuni.eventorareviews.controller;

import bg.softuni.eventorareviews.exception.ReviewNotFoundException;
import bg.softuni.eventorareviews.exception.ReviewRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ReviewNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ReviewRuleException.class)
    public ResponseEntity<Map<String, String>> businessRule(ReviewRuleException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream().findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage()).orElse("Невалидни данни.");
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
