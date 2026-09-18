package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.EstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.EstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//contem as regras de negocio relacionadas ao controle de estoque
@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ItemRepository itemRepository;

    public EstoqueService(
            EstoqueRepository estoqueRepository,
            ItemRepository itemRepository) {

        this.estoqueRepository = estoqueRepository;
        this.itemRepository = itemRepository;
    }

    public List<EstoqueResponseDTO> listarTodos() {
        return estoqueRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public EstoqueResponseDTO buscarPorId(Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Estoque não encontrado"));

        return converterParaResponse(estoque);
    }

    public EstoqueResponseDTO criar(EstoqueRequestDTO dto) {
        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Item não encontrado"));

        Estoque estoque = new Estoque();
        estoque.setItem(item);
        estoque.setQuantidade(dto.quantidade());

        Estoque estoqueSalvo = estoqueRepository.save(estoque);

        return converterParaResponse(estoqueSalvo);
    }

    public EstoqueResponseDTO atualizar(Long id, EstoqueRequestDTO dto) {
        Estoque estoqueExistente = estoqueRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Estoque não encontrado"));

        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Item não encontrado"));

        estoqueExistente.setItem(item);
        estoqueExistente.setQuantidade(dto.quantidade());

        Estoque estoqueAtualizado = estoqueRepository.save(estoqueExistente);

        return converterParaResponse(estoqueAtualizado);
    }

    public void excluir(Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Estoque não encontrado"));

        estoqueRepository.delete(estoque);
    }

    private EstoqueResponseDTO converterParaResponse(Estoque estoque) {
        return new EstoqueResponseDTO(
                estoque.getId(),
                estoque.getItem().getId(),
                estoque.getItem().getNome(),
                estoque.getQuantidade()
        );
    }
}