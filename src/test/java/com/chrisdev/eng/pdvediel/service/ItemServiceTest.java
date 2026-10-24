package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.ItemRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ItemResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.TipoItem;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.CategoriaRepository;
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

//testes unitarios das regras de negocio dos itens
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void deveListarTodosOsItens() {
        when(itemRepository.findAll()).thenReturn(List.of(item(1L, "Chave Yale")));

        List<ItemResponseDTO> resultado = itemService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Chave Yale");
        assertThat(resultado.get(0).categoriaNome()).isEqualTo("Chaves");
        verify(itemRepository).findAll();
    }

    @Test
    void deveListarItensQuandoNaoHaRegistro() {
        when(itemRepository.findAll()).thenReturn(List.of());

        assertThat(itemService.listarTodos()).isEmpty();
    }

    @Test
    void deveBuscarItemPorId() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, "Chave Yale")));

        ItemResponseDTO resultado = itemService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.preco()).isEqualByComparingTo("10.00");
        assertThat(resultado.tipo()).isEqualTo(TipoItem.PRODUTO);
        assertThat(resultado.categoriaId()).isEqualTo(1L);
    }

    @Test
    void deveLancarErroQuandoItemNaoExiste() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    @Test
    void deveBuscarItemPeloNome() {
        when(itemRepository.findByNomeContainingIgnoreCase("Yale"))
                .thenReturn(List.of(item(1L, "Chave Yale")));

        List<ItemResponseDTO> resultado = itemService.buscarPorNome("Yale");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Chave Yale");
        verify(itemRepository).findByNomeContainingIgnoreCase("Yale");
    }

    @Test
    void deveCriarItem() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria(1L, "Chaves")));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocacao -> {
            Item item = invocacao.getArgument(0);
            item.setId(10L);
            return item;
        });

        ItemResponseDTO resultado = itemService.criar(new ItemRequestDTO(
                "Chave Yale", "virgem", new BigDecimal("10.00"), TipoItem.PRODUTO, 1L));

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nome()).isEqualTo("Chave Yale");
        assertThat(resultado.descricao()).isEqualTo("virgem");
        assertThat(resultado.categoriaNome()).isEqualTo("Chaves");
        assertThat(resultado.ativo()).isTrue();
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void deveLancarErroAoCriarItemComCategoriaInexistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.criar(new ItemRequestDTO(
                "Chave Yale", "virgem", new BigDecimal("10.00"), TipoItem.PRODUTO, 99L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    void deveAtualizarItem() {
        Item item = item(1L, "Chave Yale");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(categoriaRepository.findById(2L)).thenReturn(Optional.of(categoria(2L, "Cadeados")));
        when(itemRepository.save(item)).thenReturn(item);

        ItemResponseDTO resultado = itemService.atualizar(1L, new ItemRequestDTO(
                "Chave Tetra", "modelo tetra", new BigDecimal("15.00"), TipoItem.PRODUTO, 2L));

        assertThat(resultado.nome()).isEqualTo("Chave Tetra");
        assertThat(resultado.categoriaNome()).isEqualTo("Cadeados");
        assertThat(item.getPreco()).isEqualByComparingTo("15.00");
    }

    @Test
    void deveLancarErroAoAtualizarItemInexistente() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.atualizar(99L, new ItemRequestDTO(
                "Chave Yale", "virgem", new BigDecimal("10.00"), TipoItem.PRODUTO, 1L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    @Test
    void deveLancarErroAoAtualizarItemComCategoriaInexistente() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item(1L, "Chave Yale")));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.atualizar(1L, new ItemRequestDTO(
                "Chave Yale", "virgem", new BigDecimal("10.00"), TipoItem.PRODUTO, 99L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    void deveDesativarItemAoExcluir() {
        Item item = item(1L, "Chave Yale");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        itemService.excluir(1L);

        assertThat(item.getAtivo()).isFalse();
        verify(itemRepository).save(item);
    }

    @Test
    void deveLancarErroAoExcluirItemInexistente() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Item não encontrado");
    }

    private Item item(Long id, String nome) {
        Item item = new Item();
        item.setId(id);
        item.setNome(nome);
        item.setDescricao("descricao");
        item.setPreco(new BigDecimal("10.00"));
        item.setTipo(TipoItem.PRODUTO);
        item.setCategoria(categoria(1L, "Chaves"));
        item.setAtivo(true);
        return item;
    }

    private Categoria categoria(Long id, String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setAtivo(true);
        return categoria;
    }
}
