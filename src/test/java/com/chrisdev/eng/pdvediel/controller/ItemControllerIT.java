package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.repository.CategoriaRepository;
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

//testes de integracao dos endpoints de item
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void deveListarTodosOsItens() throws Exception {
        mockMvc.perform(get("/itens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void deveBuscarItemPorId() throws Exception {
        Item item = itemPorNome("Chave Yale");

        mockMvc.perform(get("/itens/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.nome").value("Chave Yale"))
                .andExpect(jsonPath("$.tipo").value("PRODUTO"))
                .andExpect(jsonPath("$.categoriaNome").value("Chaves"));
    }

    @Test
    void deveRetornar404QuandoItemNaoExiste() throws Exception {
        mockMvc.perform(get("/itens/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveBuscarItemPeloNome() throws Exception {
        mockMvc.perform(get("/itens/buscar").param("nome", "Chave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].nome").value("Chave Yale"));
    }

    @Test
    void deveCriarItem() throws Exception {
        Long categoriaId = categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.getNome().equals("Chaves"))
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Chave969\", \"descricao\": \"Chave de teste\","
                                + "\"preco\": 12.50, \"tipo\": \"PRODUTO\", \"categoriaId\": " + categoriaId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Chave969"))
                .andExpect(jsonPath("$.preco").value(12.50))
                .andExpect(jsonPath("$.tipo").value("PRODUTO"))
                .andExpect(jsonPath("$.ativo").value(true));

        assertThat(itemPorNome("Chave969")).isNotNull();
    }

    @Test
    void deveRetornar400QuandoPrecoDoItemEstaNegativo() throws Exception {
        Long categoriaId = categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.getNome().equals("Chaves"))
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(post("/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Chave Negativa\", \"preco\": -5.00,"
                                + "\"tipo\": \"PRODUTO\", \"categoriaId\": " + categoriaId + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar400QuandoFaltarCamposObrigatoriosNoItem() throws Exception {
        mockMvc.perform(post("/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\", \"preco\": 10.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar404QuandoCategoriaDoItemNaoExiste() throws Exception {
        mockMvc.perform(post("/itens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Item Sem Categoria\", \"preco\": 10.00,"
                                + "\"tipo\": \"PRODUTO\", \"categoriaId\": 999999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveAtualizarItem() throws Exception {
        Item item = itemPorNome("Chave Tetra");
        Long categoriaId = categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.getNome().equals("Chaves"))
                .findFirst()
                .orElseThrow()
                .getId();

        mockMvc.perform(put("/itens/{id}", item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Chave Tetra Nova\", \"descricao\": \"Atualizada\","
                                + "\"preco\": 18.00, \"tipo\": \"PRODUTO\", \"categoriaId\": " + categoriaId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Chave Tetra Nova"))
                .andExpect(jsonPath("$.preco").value(18.00));
    }

    @Test
    void deveRetornar404AoAtualizarItemInexistente() throws Exception {
        mockMvc.perform(put("/itens/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Chave\", \"preco\": 10.00,"
                                + "\"tipo\": \"PRODUTO\", \"categoriaId\": 1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveDesativarItemAoExcluir() throws Exception {
        Item item = itemPorNome("Chaveiro Argola");

        mockMvc.perform(delete("/itens/{id}", item.getId()))
                .andExpect(status().isNoContent());

        assertThat(itemRepository.findById(item.getId()).orElseThrow().getAtivo()).isFalse();
    }

    @Test
    void deveRetornar404AoExcluirItemInexistente() throws Exception {
        mockMvc.perform(delete("/itens/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private Item itemPorNome(String nome) {
        return itemRepository.findByNomeContainingIgnoreCase(nome).stream()
                .filter(item -> item.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }
}
