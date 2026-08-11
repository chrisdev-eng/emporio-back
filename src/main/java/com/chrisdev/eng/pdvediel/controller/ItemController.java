package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.service.ItemService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/itens")
    public List<Item> listarTodos() {
        return itemService.listarTodos();
    }

    @GetMapping("/itens/{id}")
    public Item buscarPorId(@PathVariable Long id) {
        return itemService.buscarPorId(id);
    }

    @PostMapping("/itens")
    public Item criar(@RequestBody Item item) {
        return itemService.criar(item);
    }

    @PutMapping("/itens/{id}")
    public Item atualizar(
            @PathVariable Long id,
            @RequestBody Item item) {

        return itemService.atualizar(id, item);
    }

    @DeleteMapping("/itens/{id}")
    public void excluir(@PathVariable Long id) {
        itemService.excluir(id);
    }
}