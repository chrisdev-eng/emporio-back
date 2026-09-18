package com.chrisdev.eng.pdvediel.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException exception) {

        return criarResposta(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage()
        );
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, Object>> tratarCredenciaisInvalidas(
            CredenciaisInvalidasException exception) {

        return criarResposta(
                HttpStatus.UNAUTHORIZED,
                "Credenciais inválidas",
                exception.getMessage()
        );
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> tratarEstoqueInsuficiente(
            EstoqueInsuficienteException exception) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Estoque insuficiente",
                exception.getMessage()
        );
    }

    @ExceptionHandler(ValorRecebidoInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> tratarValorRecebidoInsuficiente(
            ValorRecebidoInsuficienteException exception) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Valor recebido insuficiente",
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErroValidacao(
            MethodArgumentNotValidException exception) {

        String mensagem = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos");

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                mensagem
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarConflitoDados(
            DataIntegrityViolationException exception) {

        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                "Já existe um registro com essas informações"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroInterno(
            Exception exception) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado"
        );
    }

    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String erro,
            String mensagem) {

        Map<String, Object> resposta = Map.of(
                "dataHora", LocalDateTime.now(),
                "status", status.value(),
                "erro", erro,
                "mensagem", mensagem
        );

        return ResponseEntity.status(status).body(resposta);
    }
}