//tüm uygulama genelinde istisna (hata) yakalama ve yönetme
package com.ecommerce.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        logger.error("Runtime error: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Runtime Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 