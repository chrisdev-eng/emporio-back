package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.service.MovimentacaoEstoqueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//controller responsável pelo registro e consulta das movimentações do estoque
@RestController
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(
            MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @GetMapping("/movimentacoes")
    public List<MovimentacaoEstoque> listarTodas() {
        return movimentacaoEstoqueService.listarTodas();
    }

    @GetMapping("/movimentacoes/{id}")
    public MovimentacaoEstoque buscarPorId(@PathVariable Long id) {
        return movimentacaoEstoqueService.buscarPorId(id);
    }

    //registra uma nova movimentação de entrada ou de saída do estoque
    @PostMapping("/movimentacoes")
    public MovimentacaoEstoque criar(
            @RequestBody MovimentacaoEstoque movimentacao) {

        return movimentacaoEstoqueService.criar(movimentacao);
    }
}