package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.ItemRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ItemResponseDTO;
import com.chrisdev.eng.pdvediel.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//controller responsável pela ops relacionadas aos itens do sistema
//permite consultar, cadastrar, atualizar e excluir itens
@RestController
@RequestMapping("/itens")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponseDTO> listarTodos() {
        return itemService.listarTodos();
    }

    @GetMapping("/{id}")
    public ItemResponseDTO buscarPorId(@PathVariable Long id) {
        return itemService.buscarPorId(id);
    }

    //busca itens pelo nome informado como parametro da req
    @GetMapping("/buscar")
    public List<ItemResponseDTO> buscarPorNome(
            @RequestParam String nome) {

        return itemService.buscarPorNome(nome);
    }

    //cadastra um novo item e retorna o recurso q foi criado
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDTO criar(
            @Valid @RequestBody ItemRequestDTO dto) {

        return itemService.criar(dto);
    }

    @PutMapping("/{id}")
    public ItemResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDTO dto) {

        return itemService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        itemService.excluir(id);
    }
}