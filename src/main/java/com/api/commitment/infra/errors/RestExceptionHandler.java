package com.api.commitment.infra.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(RuntimeException.class) // Captura qualquer RuntimeException lançada no sistema
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        // Retorna o status 400 (Bad Request) com a mensagem que você escreveu no
        // Service
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    // Record auxiliar para formatar o JSON de erro bonitinho
    private record ErrorMessage(String message) {
    }

}
