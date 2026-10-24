package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.client.ViaCepClient;
import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import com.chrisdev.eng.pdvediel.entity.Cliente;
import com.chrisdev.eng.pdvediel.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//testes de integracao dos endpoints de cliente
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @MockitoBean
    private ViaCepClient viaCepClient;

    @Test
    void deveListarTodosOsClientes() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveCriarClienteBuscandoOEnderecoPeloCep() throws Exception {
        when(viaCepClient.buscarPorCep("01001000")).thenReturn(endereco());

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cliente Novo\", \"cpf\": \"52998224725\", \"cep\": \"01001000\","
                                + "\"numero\": \"100\", \"complemento\": \"Apto 21\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Cliente Novo"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.numero").value("100"))
                .andExpect(jsonPath("$.ativo").value(true));

        assertThat(clientePorCpf("52998224725")).isNotNull();
    }

    @Test
    void deveBuscarClientePorId() throws Exception {
        Cliente cliente = cliente();

        mockMvc.perform(get("/clientes/{id}", cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()))
                .andExpect(jsonPath("$.nome").value("Cliente da Loja"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar404QuandoClienteNaoExiste() throws Exception {
        mockMvc.perform(get("/clientes/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveAtualizarCliente() throws Exception {
        Cliente cliente = cliente();
        when(viaCepClient.buscarPorCep("01002000")).thenReturn(endereco());

        mockMvc.perform(put("/clientes/{id}", cliente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cliente Atualizado\", \"cpf\": \"11122233344\","
                                + "\"cep\": \"01002000\", \"numero\": \"200\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cliente Atualizado"))
                .andExpect(jsonPath("$.numero").value("200"));
    }

    @Test
    void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
        mockMvc.perform(put("/clientes/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Fantasma\", \"cpf\": \"11122233344\","
                                + "\"cep\": \"01001000\", \"numero\": \"100\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveDesativarClienteAoExcluir() throws Exception {
        Cliente cliente = cliente();

        mockMvc.perform(delete("/clientes/{id}", cliente.getId()))
                .andExpect(status().isNoContent());

        assertThat(clienteRepository.findById(cliente.getId()).orElseThrow().getAtivo()).isFalse();
    }

    @Test
    void deveRetornar404AoExcluirClienteInexistente() throws Exception {
        mockMvc.perform(delete("/clientes/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400QuandoCpfDoClienteForInvalido() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cpf Errado\", \"cpf\": \"123\", \"cep\": \"01001000\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar400QuandoCepDoClienteForInvalido() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cep Errado\", \"cpf\": \"52998224725\", \"cep\": \"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar409QuandoCpfDoClienteJaExiste() throws Exception {
        when(viaCepClient.buscarPorCep("01001000")).thenReturn(endereco());
        cliente();

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Duplicado\", \"cpf\": \"11122233344\", \"cep\": \"01001000\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflito de dados"));
    }

    @Test
    void deveRetornar404QuandoCepNaoExiste() throws Exception {
        when(viaCepClient.buscarPorCep("00000000"))
                .thenReturn(new ViaCepResponse(
                        "00000000", null, null, null, null, null, null, null, null,
                        null, null, null, null, true));

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cep Invalido\", \"cpf\": \"52998224725\", \"cep\": \"00000000\"}"))
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

    private Cliente cliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente da Loja");
        cliente.setCpf("11122233344");
        cliente.setCep("01001000");
        cliente.setLogradouro("Praça da Sé");
        cliente.setNumero("100");
        cliente.setBairro("Sé");
        cliente.setCidade("São Paulo");
        cliente.setUf("SP");
        cliente.setAtivo(true);
        return clienteRepository.save(cliente);
    }

    private Cliente clientePorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).orElseThrow();
    }
}
