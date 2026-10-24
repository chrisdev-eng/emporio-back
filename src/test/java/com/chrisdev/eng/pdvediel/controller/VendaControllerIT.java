package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.entity.Cliente;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.repository.ClienteRepository;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import com.chrisdev.eng.pdvediel.repository.VendaRepository;
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

//testes de integracao dos endpoints de venda
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VendaControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Test
    void deveListarTodasAsVendas() throws Exception {
        mockMvc.perform(get("/vendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveRetornar404QuandoVendaNaoExiste() throws Exception {
        mockMvc.perform(get("/vendas/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCriarVendaDeProdutoBaixandoOEstoque() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 20.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 2}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(16.00))
                .andExpect(jsonPath("$.valorRecebido").value(20.00))
                .andExpect(jsonPath("$.formaPagamento").value("DINHEIRO"))
                .andExpect(jsonPath("$.usuarioNome").value("Administrador"))
                .andExpect(jsonPath("$.itens.length()").value(1))
                .andExpect(jsonPath("$.itens[0].itemNome").value("Chave Yale"));

        assertThat(estoquePorItem(item).getQuantidade()).isEqualTo(48);
        assertThat(movimentacaoEstoqueRepository.findAll()).hasSize(1);
    }

    @Test
    void deveCriarVendaDeServicoSemMexerNoEstoque() throws Exception {
        Item item = itemPorNome("Copia de Chave");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"PIX\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 10.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorTotal").value(10.00))
                .andExpect(jsonPath("$.formaPagamento").value("PIX"));

        assertThat(movimentacaoEstoqueRepository.findAll()).isEmpty();
    }

    @Test
    void deveCriarVendaAssociandoOCliente() throws Exception {
        Item item = itemPorNome("Copia de Chave");
        Usuario usuario = usuarioPorLogin("admin");
        Cliente cliente = cliente();

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"CARTAO_DEBITO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"clienteId\": " + cliente.getId() + ", \"valorRecebido\": 10.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.clienteNome").value("Cliente da Loja"));
    }

    @Test
    void deveBuscarVendaCriada() throws Exception {
        Item item = itemPorNome("Copia de Chave");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 10.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isCreated());

        Long id = vendaRepository.findAll().get(0).getId();

        mockMvc.perform(get("/vendas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.valorTotal").value(10.00))
                .andExpect(jsonPath("$.itens.length()").value(1))
                .andExpect(jsonPath("$.itens[0].itemNome").value("Copia de Chave"));
    }

    @Test
    void deveRetornar400QuandoValorRecebidoForMenorQueOTotal() throws Exception {
        Item item = itemPorNome("Copia de Chave");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 1.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Valor recebido insuficiente"));
    }

    @Test
    void deveRetornar400QuandoEstoqueNaoCobreAQuantidade() throws Exception {
        Item item = itemPorNome("Chave Yale");
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 100.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 500}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Estoque insuficiente"));
    }

    @Test
    void deveRetornar404QuandoUsuarioDaVendaNaoExiste() throws Exception {
        Item item = itemPorNome("Copia de Chave");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": 999999,"
                                + "\"valorRecebido\": 10.00,"
                                + "\"itens\": [{\"itemId\": " + item.getId() + ", \"quantidade\": 1}]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar404QuandoItemDaVendaNaoExiste() throws Exception {
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 10.00,"
                                + "\"itens\": [{\"itemId\": 999999, \"quantidade\": 1}]}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar400QuandoVendaNaoPossuirItens() throws Exception {
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(post("/vendas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formaPagamento\": \"DINHEIRO\", \"usuarioId\": " + usuario.getId() + ","
                                + "\"valorRecebido\": 10.00, \"itens\": []}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
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
