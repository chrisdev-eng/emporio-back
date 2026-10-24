package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.ItemVendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ItemVendaResponseDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.entity.Cliente;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.FormaPagamento;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.ItemVenda;
import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.entity.Perfil;
import com.chrisdev.eng.pdvediel.entity.TipoItem;
import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.entity.Venda;
import com.chrisdev.eng.pdvediel.exception.EstoqueInsuficienteException;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.exception.ValorRecebidoInsuficienteException;
import com.chrisdev.eng.pdvediel.repository.ClienteRepository;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.ItemVendaRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import com.chrisdev.eng.pdvediel.repository.VendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios das regras de negocio das vendas
@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ItemVendaRepository itemVendaRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VendaService vendaService;

    @Test
    void deveCriarVendaDeProdutoBaixandoOEstoque() {
        Usuario usuario = usuario(1L, true);
        Item item = item(1L, TipoItem.PRODUTO, true);
        Estoque estoque = estoque(10, item);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.of(estoque));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> {
            Venda venda = invocacao.getArgument(0);
            venda.setId(100L);
            return venda;
        });
        when(itemVendaRepository.findByVendaId(100L)).thenReturn(List.of());

        VendaResponseDTO resultado = vendaService.criar(requisicao(new BigDecimal("20.00"), 1L, 1L));

        assertThat(resultado.id()).isEqualTo(100L);
        assertThat(resultado.valorTotal()).isEqualByComparingTo("20.00");
        assertThat(resultado.formaPagamento()).isEqualTo(FormaPagamento.DINHEIRO);
        assertThat(resultado.usuarioNome()).isEqualTo("Administrador");
        assertThat(resultado.clienteId()).isNull();
        assertThat(estoque.getQuantidade()).isEqualTo(8);
        verify(estoqueRepository).save(estoque);

        ArgumentCaptor<MovimentacaoEstoque> captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movimentacaoEstoqueRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoMovimentacao.SAIDA);
        assertThat(captor.getValue().getQuantidade()).isEqualTo(2);
        verify(itemVendaRepository).save(any(ItemVenda.class));
    }

    @Test
    void deveCriarVendaDeServicoSemMexerNoEstoque() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, TipoItem.SERVICO, true)));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> {
            Venda venda = invocacao.getArgument(0);
            venda.setId(100L);
            return venda;
        });
        when(itemVendaRepository.findByVendaId(100L)).thenReturn(List.of());

        VendaResponseDTO resultado = vendaService.criar(requisicao(new BigDecimal("20.00"), 1L, 1L));

        assertThat(resultado.valorTotal()).isEqualByComparingTo("20.00");
        verify(estoqueRepository, never()).findByItemId(any());
        verify(movimentacaoEstoqueRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    void deveCriarVendaAssociandoOCliente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(clienteRepository.findById(7L)).thenReturn(Optional.of(cliente(7L, true)));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, TipoItem.SERVICO, true)));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(invocacao -> {
            Venda venda = invocacao.getArgument(0);
            venda.setId(100L);
            return venda;
        });
        when(itemVendaRepository.findByVendaId(100L)).thenReturn(List.of());

        VendaResponseDTO resultado = vendaService.criar(
                new VendaRequestDTO(FormaPagamento.PIX, 1L, 7L, new BigDecimal("20.00"),
                        List.of(new ItemVendaRequestDTO(1L, 2))));

        assertThat(resultado.clienteId()).isEqualTo(7L);
        assertThat(resultado.clienteNome()).isEqualTo("Cliente Teste");
        assertThat(resultado.formaPagamento()).isEqualTo(FormaPagamento.PIX);
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.criar(requisicao(new BigDecimal("20.00"), 99L, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveLancarErroQuandoUsuarioEstaInativo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, false)));

        assertThatThrownBy(() -> vendaService.criar(requisicao(new BigDecimal("20.00"), 1L, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário inativo");
    }

    @Test
    void deveLancarErroQuandoClienteNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.criar(
                new VendaRequestDTO(FormaPagamento.DINHEIRO, 1L, 99L, new BigDecimal("20.00"),
                        List.of(new ItemVendaRequestDTO(1L, 1)))))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    void deveLancarErroQuandoClienteEstaInativo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(clienteRepository.findById(7L)).thenReturn(Optional.of(cliente(7L, false)));

        assertThatThrownBy(() -> vendaService.criar(
                new VendaRequestDTO(FormaPagamento.DINHEIRO, 1L, 7L, new BigDecimal("20.00"),
                        List.of(new ItemVendaRequestDTO(1L, 1)))))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente inativo");
    }

    @Test
    void deveLancarErroQuandoItemNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.criar(requisicao(new BigDecimal("20.00"), 1L, 99L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    @Test
    void deveLancarErroQuandoItemEstaInativo() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, TipoItem.SERVICO, false)));

        assertThatThrownBy(() -> vendaService.criar(requisicao(new BigDecimal("20.00"), 1L, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item inativo");
    }

    @Test
    void deveLancarErroQuandoProdutoNaoPossuiEstoque() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, TipoItem.PRODUTO, true)));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.criar(requisicao(new BigDecimal("20.00"), 1L, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Estoque não encontrado para o produto");
    }

    @Test
    void deveLancarErroQuandoEstoqueNaoCobreAQuantidade() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, TipoItem.PRODUTO, true)));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.of(estoque(1, item(1L, TipoItem.PRODUTO, true))));

        assertThatThrownBy(() -> vendaService.criar(
                new VendaRequestDTO(FormaPagamento.DINHEIRO, 1L, null, new BigDecimal("50.00"),
                        List.of(new ItemVendaRequestDTO(1L, 5)))))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessage("Estoque insuficiente para o item: Chave Yale");
    }

    @Test
    void deveLancarErroQuandoValorRecebidoEMenorQueOTotal() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, true)));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, TipoItem.SERVICO, true)));

        assertThatThrownBy(() -> vendaService.criar(requisicao(new BigDecimal("5.00"), 1L, 1L)))
                .isInstanceOf(ValorRecebidoInsuficienteException.class)
                .hasMessage("O valor recebido é menor que o valor total da venda");
    }

    @Test
    void deveListarTodasAsVendas() {
        when(vendaRepository.findAll()).thenReturn(List.of(venda(1L)));
        when(itemVendaRepository.findByVendaId(1L)).thenReturn(List.of(itemVenda()));

        List<VendaResponseDTO> resultado = vendaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).itens()).hasSize(1);
        assertThat(resultado.get(0).itens().get(0).subtotal()).isEqualByComparingTo("20.00");
        verify(vendaRepository).findAll();
    }

    @Test
    void deveBuscarVendaPorId() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda(1L)));
        when(itemVendaRepository.findByVendaId(1L)).thenReturn(List.of(itemVenda()));

        VendaResponseDTO resultado = vendaService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.valorTotal()).isEqualByComparingTo("20.00");
        assertThat(resultado.clienteNome()).isEqualTo("Cliente Teste");
    }

    @Test
    void deveLancarErroQuandoVendaNaoExiste() {
        when(vendaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vendaService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Venda não encontrada");
    }

    private VendaRequestDTO requisicao(BigDecimal valorRecebido, Long usuarioId, Long itemId) {
        return new VendaRequestDTO(FormaPagamento.DINHEIRO, usuarioId, null, valorRecebido,
                List.of(new ItemVendaRequestDTO(itemId, 2)));
    }

    private Venda venda(Long id) {
        Venda venda = new Venda();
        venda.setId(id);
        venda.setData(LocalDateTime.now());
        venda.setValorTotal(new BigDecimal("20.00"));
        venda.setValorRecebido(new BigDecimal("20.00"));
        venda.setFormaPagamento(FormaPagamento.DINHEIRO);
        venda.setUsuario(usuario(1L, true));
        venda.setCliente(cliente(7L, true));
        return venda;
    }

    private ItemVenda itemVenda() {
        ItemVenda itemVenda = new ItemVenda();
        itemVenda.setId(1L);
        itemVenda.setItem(item(1L, TipoItem.SERVICO, true));
        itemVenda.setQuantidade(2);
        itemVenda.setPrecoUnitario(new BigDecimal("10.00"));
        return itemVenda;
    }

    private Cliente cliente(Long id, boolean ativo) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678901");
        cliente.setCep("01001000");
        cliente.setAtivo(ativo);
        return cliente;
    }

    private Usuario usuario(Long id, boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Administrador");
        usuario.setLogin("admin");
        usuario.setSenha("hash-cripografado");
        usuario.setPerfil(Perfil.DONO);
        usuario.setAtivo(ativo);
        return usuario;
    }

    private Estoque estoque(Integer quantidade, Item item) {
        Estoque estoque = new Estoque();
        estoque.setId(1L);
        estoque.setItem(item);
        estoque.setQuantidade(quantidade);
        return estoque;
    }

    private Item item(Long id, TipoItem tipo, boolean ativo) {
        Item item = new Item();
        item.setId(id);
        item.setNome("Chave Yale");
        item.setPreco(new BigDecimal("10.00"));
        item.setTipo(tipo);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Chaves");
        item.setCategoria(categoria);

        item.setAtivo(ativo);
        return item;
    }
}
