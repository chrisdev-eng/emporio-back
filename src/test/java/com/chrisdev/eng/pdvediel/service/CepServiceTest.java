package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.client.ViaCepClient;
import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios da consulta de cep
@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private ViaCepClient viaCepClient;

    @InjectMocks
    private CepService cepService;

    @Test
    void deveRetornarEnderecoDoCep() {
        ViaCepResponse resposta = resposta(false);
        when(viaCepClient.buscarPorCep("01001000")).thenReturn(resposta);

        ViaCepResponse resultado = cepService.buscarPorCep("01001000");

        assertThat(resultado).isEqualTo(resposta);
        assertThat(resultado.logradouro()).isEqualTo("Praça da Sé");
        verify(viaCepClient).buscarPorCep("01001000");
    }

    @Test
    void deveLancarErroQuandoCepNaoExiste() {
        when(viaCepClient.buscarPorCep("00000000")).thenReturn(resposta(true));

        assertThatThrownBy(() -> cepService.buscarPorCep("00000000"))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("CEP não encontrado");
    }

    private ViaCepResponse resposta(boolean erro) {
        return new ViaCepResponse(
                "01001-000",
                "Praça da Sé",
                "lado ímpar",
                "unidade",
                "Sé",
                "São Paulo",
                "SP",
                "São Paulo",
                "Sudeste",
                "3550308",
                "1004",
                "11",
                "7107",
                erro
        );
    }
}
