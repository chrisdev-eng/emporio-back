package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.client.ViaCepClient;
import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//testes de integracao do endpoint de consulta de cep
@SpringBootTest
@AutoConfigureMockMvc
class CepControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ViaCepClient viaCepClient;

    @Test
    void deveBuscarEnderecoPeloCep() throws Exception {
        when(viaCepClient.buscarPorCep("01001000")).thenReturn(endereco());

        mockMvc.perform(get("/cep/{cep}", "01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.erro").value(false));
    }

    @Test
    void deveRetornar404QuandoCepNaoExiste() throws Exception {
        when(viaCepClient.buscarPorCep("00000000"))
                .thenReturn(new ViaCepResponse(
                        "00000000", null, null, null, null, null, null, null, null,
                        null, null, null, null, true));

        mockMvc.perform(get("/cep/{cep}", "00000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private ViaCepResponse endereco() {
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
                false
        );
    }
}
