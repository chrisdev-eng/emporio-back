package com.chrisdev.eng.pdvediel.exception;

import com.chrisdev.eng.pdvediel.controller.CategoriaController;
import com.chrisdev.eng.pdvediel.controller.dto.CategoriaRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//testes unitarios do tratamento global de erros da api
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void deveRetornar404QuandoRecursoNaoEncontrado() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarRecursoNaoEncontrado(new RecursoNaoEncontradoException("Item não encontrado"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody())
                .containsEntry("status", 404)
                .containsEntry("erro", "Recurso não encontrado")
                .containsEntry("mensagem", "Item não encontrado");
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidas() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarCredenciaisInvalidas(new CredenciaisInvalidasException("Login ou senha inválidos"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resposta.getBody())
                .containsEntry("status", 401)
                .containsEntry("erro", "Credenciais inválidas")
                .containsEntry("mensagem", "Login ou senha inválidos");
    }

    @Test
    void deveRetornar400QuandoEstoqueInsuficiente() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarEstoqueInsuficiente(new EstoqueInsuficienteException("Estoque insuficiente"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody())
                .containsEntry("status", 400)
                .containsEntry("erro", "Estoque insuficiente")
                .containsEntry("mensagem", "Estoque insuficiente");
    }

    @Test
    void deveRetornar400QuandoValorRecebidoInsuficiente() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarValorRecebidoInsuficiente(
                        new ValorRecebidoInsuficienteException("O valor recebido é menor que o valor total"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody())
                .containsEntry("status", 400)
                .containsEntry("erro", "Valor recebido insuficiente");
    }

    @Test
    void deveRetornar400QuandoHouverErroDeValidacao() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "categoriaRequestDTO");
        bindingResult.addError(new FieldError(
                "categoriaRequestDTO", "nome", "O nome da categoria é obrigatório"));

        MethodParameter parameter = new MethodParameter(
                CategoriaController.class.getDeclaredMethod("criar", CategoriaRequestDTO.class), 0);

        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarErroValidacao(new MethodArgumentNotValidException(parameter, bindingResult));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody())
                .containsEntry("status", 400)
                .containsEntry("erro", "Erro de validação")
                .containsEntry("mensagem", "nome: O nome da categoria é obrigatório");
    }

    @Test
    void deveRetornar400QuandoValidacaoNaoTiverMensagemDeCampo() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "categoriaRequestDTO");

        MethodParameter parameter = new MethodParameter(
                CategoriaController.class.getDeclaredMethod("criar", CategoriaRequestDTO.class), 0);

        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarErroValidacao(new MethodArgumentNotValidException(parameter, bindingResult));

        assertThat(resposta.getBody()).containsEntry("mensagem", "Dados inválidos");
    }

    @Test
    void deveRetornar409QuandoHouverConflitoDeDados() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarConflitoDados(new org.springframework.dao.DataIntegrityViolationException("violacao"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody())
                .containsEntry("status", 409)
                .containsEntry("erro", "Conflito de dados")
                .containsEntry("mensagem", "Já existe um registro com essas informações");
    }

    @Test
    void deveRetornar500QuandoOcorrerErroInesperado() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarErroInterno(new RuntimeException("falha inesperada"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody())
                .containsEntry("status", 500)
                .containsEntry("erro", "Erro interno")
                .containsEntry("mensagem", "Ocorreu um erro inesperado");
    }

    @Test
    void deveMontarARespostaComDataHora() {
        ResponseEntity<Map<String, Object>> resposta = globalExceptionHandler
                .tratarRecursoNaoEncontrado(new RecursoNaoEncontradoException("Item não encontrado"));

        assertThat(resposta.getBody()).containsKey("dataHora");
    }
}
