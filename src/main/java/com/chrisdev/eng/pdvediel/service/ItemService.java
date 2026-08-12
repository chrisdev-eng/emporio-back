package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> listarTodos() {
        return itemRepository.findAll();
    }

    public Item buscarPorId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
    }

    public Item criar(Item item) {
        return itemRepository.save(item);
    }

    public Item atualizar(Long id, Item item) {
        Item itemExistente = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        itemExistente.setNome(item.getNome());
        itemExistente.setDescricao(item.getDescricao());
        itemExistente.setPreco(item.getPreco());
        itemExistente.setTipo(item.getTipo());
        itemExistente.setCategoria(item.getCategoria());
        itemExistente.setAtivo(item.getAtivo());

        return itemRepository.save(itemExistente);
    }

    public void excluir(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        itemRepository.delete(item);
    }
}