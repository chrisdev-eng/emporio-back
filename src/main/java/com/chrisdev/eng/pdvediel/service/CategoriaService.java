package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.CategoriaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.CategoriaResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;

import java.util.List;

//contém as regras de negocio relacionadas ás categorias
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria não encontrada"));
        return converterParaResponse(categoria);
    }

    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {

        Categoria categoria = new Categoria();

        categoria.setNome(dto.nome());

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return converterParaResponse(categoriaSalva);
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {

        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria não encontrada"));
        categoriaExistente.setNome(dto.nome());

        Categoria categoriaAtualizada =
                categoriaRepository.save(categoriaExistente);

        return converterParaResponse(categoriaAtualizada);
    }

    public void excluir(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Categoria não encontrada"));
        categoriaRepository.delete(categoria);
    }

    private CategoriaResponseDTO converterParaResponse(Categoria categoria) {

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getAtivo()
        );
    }

    public List<CategoriaResponseDTO> buscarPorNome(String nome) {
        return categoriaRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }
}