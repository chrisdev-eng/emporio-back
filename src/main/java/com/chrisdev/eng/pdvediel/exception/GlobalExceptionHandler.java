package com.chrisdev.eng.pdvediel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<String> tratarCredenciaisInvalidas(
            CredenciaisInvalidasException exception) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exception.getMessage());
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<String> tratarEstoqueInsuficiente(
            EstoqueInsuficienteException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<String> tratarErroValidacao(
        MethodArgumentNotValidException exception) {

      String mensagem = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
            .findFirst()
            .orElse("Dados inválidos");

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mensagem);
    }
}