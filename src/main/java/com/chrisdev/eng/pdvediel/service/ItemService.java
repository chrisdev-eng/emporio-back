package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.ItemRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ItemResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Categoria;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.repository.CategoriaRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//contem as regras de negocio relacionado aos itens
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoriaRepository categoriaRepository;

    public ItemService(
            ItemRepository itemRepository,
            CategoriaRepository categoriaRepository) {

        this.itemRepository = itemRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ItemResponseDTO> listarTodos() {
        return itemRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ItemResponseDTO buscarPorId(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        return converterParaResponse(item);
    }

    public List<ItemResponseDTO> buscarPorNome(String nome) {
        return itemRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ItemResponseDTO criar(ItemRequestDTO dto) {

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Item item = new Item();

        item.setNome(dto.nome());
        item.setDescricao(dto.descricao());
        item.setPreco(dto.preco());
        item.setTipo(dto.tipo());
        item.setCategoria(categoria);

        Item itemSalvo = itemRepository.save(item);

        return converterParaResponse(itemSalvo);
    }

    public ItemResponseDTO atualizar(Long id, ItemRequestDTO dto) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        item.setNome(dto.nome());
        item.setDescricao(dto.descricao());
        item.setPreco(dto.preco());
        item.setTipo(dto.tipo());
        item.setCategoria(categoria);

        Item itemAtualizado = itemRepository.save(item);

        return converterParaResponse(itemAtualizado);
    }

    public void excluir(Long id) {

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        itemRepository.delete(item);
    }

    private ItemResponseDTO converterParaResponse(Item item) {

        return new ItemResponseDTO(
                item.getId(),
                item.getNome(),
                item.getDescricao(),
                item.getPreco(),
                item.getTipo(),
                item.getCategoria().getId(),
                item.getCategoria().getNome(),
                item.getAtivo()
        );
    }
}