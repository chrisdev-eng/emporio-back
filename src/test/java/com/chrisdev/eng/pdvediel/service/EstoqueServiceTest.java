package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.EstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.EstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.TipoItem;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

//testes unitarios das regras de negocio do controle de estoque
@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    @Test
    void deveListarTodosOsEstoque() {
        when(estoqueRepository.findAll()).thenReturn(List.of(estoque(1L, 10)));

        List<EstoqueResponseDTO> resultado = estoqueService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).quantidade()).isEqualTo(10);
        assertThat(resultado.get(0).itemNome()).isEqualTo("Chave Yale");
        verify(estoqueRepository).findAll();
    }

    @Test
    void deveListarEstoqueQuandoNaoHaRegistro() {
        when(estoqueRepository.findAll()).thenReturn(List.of());

        assertThat(estoqueService.listarTodos()).isEmpty();
    }

    @Test
    void deveBuscarEstoquePorId() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque(1L, 10)));

        EstoqueResponseDTO resultado = estoqueService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.itemId()).isEqualTo(1L);
        assertThat(resultado.quantidade()).isEqualTo(10);
    }

    @Test
    void deveLancarErroQuandoEstoqueNaoExiste() {
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Estoque não encontrado");
    }

    @Test
    void deveCriarEstoque() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item()));
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(invocacao -> {
            Estoque estoque = invocacao.getArgument(0);
            estoque.setId(10L);
            return estoque;
        });

        EstoqueResponseDTO resultado = estoqueService.criar(new EstoqueRequestDTO(1L, 25));

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.quantidade()).isEqualTo(25);
        verify(estoqueRepository).save(any(Estoque.class));
    }

    @Test
    void deveLancarErroAoCriarEstoqueComItemInexistente() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.criar(new EstoqueRequestDTO(99L, 25)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    @Test
    void deveAtualizarEstoque() {
        Estoque estoque = estoque(1L, 10);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item(2L, "Chave Tetra")));
        when(estoqueRepository.save(estoque)).thenReturn(estoque);

        EstoqueResponseDTO resultado = estoqueService.atualizar(1L, new EstoqueRequestDTO(2L, 40));

        assertThat(resultado.quantidade()).isEqualTo(40);
        assertThat(resultado.itemNome()).isEqualTo("Chave Tetra");
    }

    @Test
    void deveLancarErroAoAtualizarEstoqueInexistente() {
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.atualizar(99L, new EstoqueRequestDTO(1L, 40)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Estoque não encontrado");
    }

    @Test
    void deveLancarErroAoAtualizarEstoqueComItemInexistente() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque(1L, 10)));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.atualizar(1L, new EstoqueRequestDTO(99L, 40)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    @Test
    void deveExcluirEstoque() {
        Estoque estoque = estoque(1L, 10);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        estoqueService.excluir(1L);

        verify(estoqueRepository).delete(estoque);
    }

    @Test
    void deveLancarErroAoExcluirEstoqueInexistente() {
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Estoque não encontrado");
    }

    private Estoque estoque(Long id, Integer quantidade) {
        Estoque estoque = new Estoque();
        estoque.setId(id);
        estoque.setItem(item());
        estoque.setQuantidade(quantidade);
        return estoque;
    }

    private Item item() {
        return item(1L, "Chave Yale");
    }

    private Item item(Long id, String nome) {
        Item item = new Item();
        item.setId(id);
        item.setNome(nome);
        item.setPreco(new BigDecimal("10.00"));
        item.setTipo(TipoItem.PRODUTO);

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Chaves");
        item.setCategoria(categoria);

        item.setAtivo(true);
        return item;
    }
}
