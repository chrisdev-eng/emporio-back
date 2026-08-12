package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.service.EstoqueService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/estoques")
    public List<Estoque> listarTodos() {
        return estoqueService.listarTodos();
    }

    @GetMapping("/estoques/{id}")
    public Estoque buscarPorId(@PathVariable Long id) {
        return estoqueService.buscarPorId(id);
    }

    @PostMapping("/estoques")
    public Estoque criar(@RequestBody Estoque estoque) {
        return estoqueService.criar(estoque);
    }

    @PutMapping("/estoques/{id}")
    public Estoque atualizar(
            @PathVariable Long id,
            @RequestBody Estoque estoque) {

        return estoqueService.atualizar(id, estoque);
    }

    @DeleteMapping("/estoques/{id}")
    public void excluir(@PathVariable Long id) {
        estoqueService.excluir(id);
    }
}