package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.CategoriaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.CategoriaResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios das regras de negocio das categorias
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void deveListarTodasAsCategorias() {
        when(categoriaRepository.findAll())
                .thenReturn(List.of(categoria(1L, "Chaves"), categoria(2L, "Cadeados")));

        List<CategoriaResponseDTO> resultado = categoriaService.listarTodas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Chaves");
        assertThat(resultado.get(1).nome()).isEqualTo("Cadeados");
        assertThat(resultado.get(0).ativo()).isTrue();
        verify(categoriaRepository).findAll();
    }

    @Test
    void deveListarCategoriasQuandoNaoHaRegistro() {
        when(categoriaRepository.findAll()).thenReturn(List.of());

        assertThat(categoriaService.listarTodas()).isEmpty();
    }

    @Test
    void deveBuscarCategoriaPorId() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria(1L, "Chaves")));

        CategoriaResponseDTO resultado = categoriaService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Chaves");
    }

    @Test
    void deveLancarErroQuandoCategoriaNaoExiste() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    void deveCriarCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocacao -> {
            Categoria categoria = invocacao.getArgument(0);
            categoria.setId(10L);
            return categoria;
        });

        CategoriaResponseDTO resultado = categoriaService.criar(new CategoriaRequestDTO("Chaves"));

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nome()).isEqualTo("Chaves");
        assertThat(resultado.ativo()).isTrue();
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void deveAtualizarCategoria() {
        Categoria categoria = categoria(1L, "Chaves");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        CategoriaResponseDTO resultado = categoriaService.atualizar(1L, new CategoriaRequestDTO("Cadeados"));

        assertThat(resultado.nome()).isEqualTo("Cadeados");
        assertThat(categoria.getNome()).isEqualTo("Cadeados");
    }

    @Test
    void deveLancarErroAoAtualizarCategoriaInexistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.atualizar(99L, new CategoriaRequestDTO("Cadeados")))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    void deveDesativarCategoriaAoExcluir() {
        Categoria categoria = categoria(1L, "Chaves");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        categoriaService.excluir(1L);

        assertThat(categoria.getAtivo()).isFalse();
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void deveLancarErroAoExcluirCategoriaInexistente() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    void deveBuscarCategoriaPeloNome() {
        when(categoriaRepository.findByNomeContainingIgnoreCase("Chav"))
                .thenReturn(List.of(categoria(1L, "Chaves")));

        List<CategoriaResponseDTO> resultado = categoriaService.buscarPorNome("Chav");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Chaves");
        verify(categoriaRepository).findByNomeContainingIgnoreCase("Chav");
    }

    private Categoria categoria(Long id, String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        categoria.setAtivo(true);
        return categoria;
    }
}
