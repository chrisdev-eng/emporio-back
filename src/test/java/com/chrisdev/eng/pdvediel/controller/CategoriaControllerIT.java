package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//testes de integracao dos endpoints de categoria
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoriaControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void deveListarTodasAsCategorias() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        Categoria categoria = categoriaPorNome("Chaves");

        mockMvc.perform(get("/categorias/{id}", categoria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoria.getId()))
                .andExpect(jsonPath("$.nome").value("Chaves"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoExiste() throws Exception {
        mockMvc.perform(get("/categorias/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCriarCategoria() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Marcadores\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Marcadores"))
                .andExpect(jsonPath("$.ativo").value(true));

        Categoria criada = categoriaPorNome("Marcadores");
        org.assertj.core.api.Assertions.assertThat(criada).isNotNull();
    }

    @Test
    void deveRetornar400QuandoNomeDaCategoriaEstaVazio() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar409QuandoNomeDaCategoriaJaExiste() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Chaves\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflito de dados"));
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        Categoria categoria = categoriaPorNome("Cadeados");

        mockMvc.perform(put("/categorias/{id}", categoria.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cadeados e Correntes\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cadeados e Correntes"));
    }

    @Test
    void deveRetornar404AoAtualizarCategoriaInexistente() throws Exception {
        mockMvc.perform(put("/categorias/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Cadeados\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveDesativarCategoriaAoExcluir() throws Exception {
        Categoria categoria = categoriaPorNome("Acessorios");

        mockMvc.perform(delete("/categorias/{id}", categoria.getId()))
                .andExpect(status().isNoContent());

        Categoria excluida = categoriaRepository.findById(categoria.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(excluida.getAtivo()).isFalse();
    }

    @Test
    void deveRetornar404AoExcluirCategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/categorias/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveBuscarCategoriaPeloNome() throws Exception {
        mockMvc.perform(get("/categorias/buscar").param("nome", "Chav"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Chaves"));
    }

    private Categoria categoriaPorNome(String nome) {
        return categoriaRepository.findAll().stream()
                .filter(categoria -> categoria.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }
}
