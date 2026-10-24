package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//testes de integracao dos endpoints de movimentacao de estoque
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MovimentacaoEstoqueControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveListarTodasAsMovimentacoes() throws Exception {
        mockMvc.perform(get("/movimentacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveRetornar404QuandoMovimentacaoNaoExiste() throws Exception {
        mockMvc.perform(get("/movimentacoes/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRegistrarEntradaNoEstoque() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 10,"
                                + "\"tipo\": \"ENTRADA\", \"usuarioId\": " + usuario.getId() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantidade").value(10))
                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                .andExpect(jsonPath("$.itemNome").value("Chave Yale"))
                .andExpect(jsonPath("$.usuarioNome").value("Administrador"));

        assertThat(movimentacaoEstoqueRepository.findAll()).hasSize(1);
        assertThat(estoquePorItem(item).getQuantidade()).isEqualTo(60);
    }

    @Test
    void deveRegistrarSaidaDoEstoque() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 5,"
                                + "\"tipo\": \"SAIDA\", \"usuarioId\": " + usuario.getId() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SAIDA"));

        assertThat(estoquePorItem(item).getQuantidade()).isEqualTo(45);
    }

    @Test
    void deveBuscarMovimentacaoCriada() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 2,"
                                + "\"tipo\": \"ENTRADA\", \"usuarioId\": " + usuario.getId() + "}"))
                .andExpect(status().isCreated());

        Long id = movimentacaoEstoqueRepository.findAll().get(0).getId();

        mockMvc.perform(get("/movimentacoes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.quantidade").value(2));
    }

    @Test
    void deveRetornar400QuandoEstoqueNaoCobreASaida() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 500,"
                                + "\"tipo\": \"SAIDA\", \"usuarioId\": " + usuario.getId() + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Estoque insuficiente"));
    }

    @Test
    void deveRetornar400QuandoQuantidadeDaMovimentacaoForZero() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 0,"
                                + "\"tipo\": \"ENTRADA\", \"usuarioId\": " + usuario.getId() + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar404QuandoItemDaMovimentacaoNaoExiste() throws Exception {
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 999999, \"quantidade\": 1,"
                                + "\"tipo\": \"ENTRADA\", \"usuarioId\": " + usuario.getId() + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar404QuandoUsuarioDaMovimentacaoNaoExiste() throws Exception {
        Item item = itemPorNome("Chave Yale");

        mockMvc.perform(post("/movimentacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": " + item.getId() + ", \"quantidade\": 1,"
                                + "\"tipo\": \"ENTRADA\", \"usuarioId\": 999999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private Estoque estoquePorItem(Item item) {
        return estoqueRepository.findByItemId(item.getId()).orElseThrow();
    }

    private Item itemPorNome(String nome) {
        return itemRepository.findByNomeContainingIgnoreCase(nome).stream()
                .filter(item -> item.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    private Usuario usuarioPorLogin(String login) {
        return usuarioRepository.findByLogin(login).orElseThrow();
    }
}
