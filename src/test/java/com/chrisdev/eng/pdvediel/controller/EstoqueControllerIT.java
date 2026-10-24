package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//testes de integracao dos endpoints de estoque
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EstoqueControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void deveListarTodosOsEstoques() throws Exception {
        mockMvc.perform(get("/estoques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void deveBuscarEstoquePorId() throws Exception {
        Estoque estoque = estoquePorItem("Chave Yale");

        mockMvc.perform(get("/estoques/{id}", estoque.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(estoque.getId()))
                .andExpect(jsonPath("$.itemNome").value("Chave Yale"))
                .andExpect(jsonPath("$.quantidade").value(50));
    }

    @Test
    void deveRetornar404QuandoEstoqueNaoExiste() throws Exception {
        mockMvc.perform(get("/estoques/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCriarEstoque() throws Exception {
        Item item = itemPorNome("Copia de Chave");

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 12}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(item.getId()))
                .andExpect(jsonPath("$.itemNome").value("Copia de Chave"))
                .andExpect(jsonPath("$.quantidade").value(12));

        assertThat(estoqueRepository.findByItemId(item.getId())).isPresent();
    }

    @Test
    void deveRetornar404QuandoItemDoEstoqueNaoExiste() throws Exception {
        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 999999, \"quantidade\": 12}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400QuandoQuantidadeDoEstoqueForNegativa() throws Exception {
        Item item = itemPorNome("Copia de Chave");

        mockMvc.perform(post("/estoques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": -3}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveAtualizarEstoque() throws Exception {
        Estoque estoque = estoquePorItem("Cadeado 30mm");

        mockMvc.perform(put("/estoques/{id}", estoque.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + estoque.getItem().getId() + ", \"quantidade\": 99}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade").value(99));
    }

    @Test
    void deveRetornar404AoAtualizarEstoqueInexistente() throws Exception {
        Item item = itemPorNome("Chave Yale");

        mockMvc.perform(put("/estoques/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveExcluirEstoque() throws Exception {
        Estoque estoque = estoquePorItem("Chaveiro Argola");

        mockMvc.perform(delete("/estoques/{id}", estoque.getId()))
                .andExpect(status().isNoContent());

        assertThat(estoqueRepository.findById(estoque.getId())).isEmpty();
    }

    @Test
    void deveRetornar404AoExcluirEstoqueInexistente() throws Exception {
        mockMvc.perform(delete("/estoques/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private Estoque estoquePorItem(String nomeItem) {
        return estoqueRepository.findByItemId(itemPorNome(nomeItem).getId()).orElseThrow();
    }

    private Item itemPorNome(String nome) {
        return itemRepository.findByNomeContainingIgnoreCase(nome).stream()
                .filter(item -> item.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }
}
