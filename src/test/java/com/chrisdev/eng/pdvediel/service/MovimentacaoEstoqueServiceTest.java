package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.entity.Perfil;
import com.chrisdev.eng.pdvediel.entity.TipoItem;
import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.exception.EstoqueInsuficienteException;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios das regras de negocio das movimentacoes de estoque
@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Test
    void deveListarTodasAsMovimentacoes() {
        when(movimentacaoEstoqueRepository.findAll()).thenReturn(List.of(movimentacao(1L, 5)));

        List<MovimentacaoEstoqueResponseDTO> resultado = movimentacaoEstoqueService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).quantidade()).isEqualTo(5);
        assertThat(resultado.get(0).itemNome()).isEqualTo("Chave Yale");
        assertThat(resultado.get(0).usuarioNome()).isEqualTo("Administrador");
        verify(movimentacaoEstoqueRepository).findAll();
    }

    @Test
    void deveListarMovimentacoesQuandoNaoHaRegistro() {
        when(movimentacaoEstoqueRepository.findAll()).thenReturn(List.of());

        assertThat(movimentacaoEstoqueService.listarTodas()).isEmpty();
    }

    @Test
    void deveBuscarMovimentacaoPorId() {
        when(movimentacaoEstoqueRepository.findById(1L)).thenReturn(Optional.of(movimentacao(1L, 5)));

        MovimentacaoEstoqueResponseDTO resultado = movimentacaoEstoqueService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.tipo()).isEqualTo(TipoMovimentacao.ENTRADA);
        assertThat(resultado.data()).isNotNull();
    }

    @Test
    void deveLancarErroQuandoMovimentacaoNaoExiste() {
        when(movimentacaoEstoqueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimentacaoEstoqueService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Movimentação não encontrada");
    }

    @Test
    void deveSomarQuantidadeNoEstoqueNaEntrada() {
        Estoque estoque = estoque(10);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item()));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.of(estoque));
        when(movimentacaoEstoqueRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocacao -> {
            MovimentacaoEstoque movimentacao = invocacao.getArgument(0);
            movimentacao.setId(1L);
            return movimentacao;
        });

        MovimentacaoEstoqueResponseDTO resultado = movimentacaoEstoqueService.criar(
                new MovimentacaoEstoqueRequestDTO(1L, 5, TipoMovimentacao.ENTRADA, 1L));

        assertThat(resultado.quantidade()).isEqualTo(5);
        assertThat(resultado.tipo()).isEqualTo(TipoMovimentacao.ENTRADA);
        assertThat(estoque.getQuantidade()).isEqualTo(15);
        verify(estoqueRepository).save(estoque);
    }

    @Test
    void deveSubtrairQuantidadeDoEstoqueNaSaida() {
        Estoque estoque = estoque(10);
        Item item = item();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.of(estoque));
        when(movimentacaoEstoqueRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocacao -> {
            MovimentacaoEstoque movimentacao = invocacao.getArgument(0);
            movimentacao.setId(1L);
            return movimentacao;
        });

        movimentacaoEstoqueService.criar(
                new MovimentacaoEstoqueRequestDTO(1L, 4, TipoMovimentacao.SAIDA, 1L));

        assertThat(estoque.getQuantidade()).isEqualTo(6);

        ArgumentCaptor<MovimentacaoEstoque> captor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(movimentacaoEstoqueRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo(TipoMovimentacao.SAIDA);
        assertThat(captor.getValue().getItem()).isSameAs(item);
    }

    @Test
    void deveLancarErroQuandoEstoqueNaoCobreASaida() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item()));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.of(estoque(3)));

        assertThatThrownBy(() -> movimentacaoEstoqueService.criar(
                new MovimentacaoEstoqueRequestDTO(1L, 5, TipoMovimentacao.SAIDA, 1L)))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessage("Estoque insuficiente");
    }

    @Test
    void deveLancarErroQuandoItemNaoExiste() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimentacaoEstoqueService.criar(
                new MovimentacaoEstoqueRequestDTO(99L, 5, TipoMovimentacao.ENTRADA, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item()));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimentacaoEstoqueService.criar(
                new MovimentacaoEstoqueRequestDTO(1L, 5, TipoMovimentacao.ENTRADA, 99L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveLancarErroQuandoItemNaoPossuiEstoque() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item()));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));
        when(estoqueRepository.findByItemId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimentacaoEstoqueService.criar(
                new MovimentacaoEstoqueRequestDTO(1L, 5, TipoMovimentacao.ENTRADA, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Estoque não encontrado para o item");
    }

    private MovimentacaoEstoque movimentacao(Long id, Integer quantidade) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(id);
        movimentacao.setItem(item());
        movimentacao.setQuantidade(quantidade);
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setData(java.time.LocalDateTime.now());
        movimentacao.setUsuario(usuario());
        return movimentacao;
    }

    private Estoque estoque(Integer quantidade) {
        Estoque estoque = new Estoque();
        estoque.setId(1L);
        estoque.setItem(item());
        estoque.setQuantidade(quantidade);
        return estoque;
    }

    private Item item() {
        Item item = new Item();
        item.setId(1L);
        item.setNome("Chave Yale");
        item.setPreco(new BigDecimal("10.00"));
        item.setTipo(TipoItem.PRODUTO);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Chaves");
        item.setCategoria(categoria);

        item.setAtivo(true);
        return item;
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Administrador");
        usuario.setLogin("admin");
        usuario.setSenha("hash-cripografado");
        usuario.setPerfil(Perfil.DONO);
        usuario.setAtivo(true);
        return usuario;
    }
}
