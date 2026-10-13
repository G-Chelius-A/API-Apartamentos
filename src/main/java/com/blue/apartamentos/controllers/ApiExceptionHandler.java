package com.blue.apartamentos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Manejador global de excepciones para todos los controladores REST.
// Convierte excepciones de negocio en respuestas HTTP con cuerpo JSON.
@RestControllerAdvice
public class ApiExceptionHandler {

    // IllegalArgumentException → 400 Bad Request (datos inválidos o faltantes)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }

    // IllegalStateException → 409 Conflict (regla de negocio violada)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", exception.getMessage()));
    }

    // NoSuchElementException → 404 Not Found (recurso inexistente)
    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(java.util.NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    // MethodArgumentNotValidException → 400 con detalle de cada campo fallido (Bean Validation)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(org.springframework.web.bind.MethodArgumentNotValidException exception) {
        java.util.Map<String, String> errores = new java.util.HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> 
            errores.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Error de validación de campos", "detalles", errores));
    }

    // ConstraintViolationException → 400 con lista de violaciones de restricción JPA/Jakarta
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(jakarta.validation.ConstraintViolationException exception) {
        java.util.List<String> errores = exception.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Violación de restricciones de validación", "detalles", errores));
    }
}