package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

//controller responsável pelo registro e consulta das movimentações do estoque
@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "true"
)
@RequestMapping("/movimentacoes")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(
            MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @GetMapping
    public List<MovimentacaoEstoqueResponseDTO> listarTodas() {
        return movimentacaoEstoqueService.listarTodas();
    }

    @GetMapping("/{id}")
    public MovimentacaoEstoqueResponseDTO buscarPorId(
            @PathVariable Long id) {

        return movimentacaoEstoqueService.buscarPorId(id);
    }

    //registra uma nova movimentação de entrada ou de saída do estoque
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimentacaoEstoqueResponseDTO criar(
            @Valid @RequestBody MovimentacaoEstoqueRequestDTO dto) {

        return movimentacaoEstoqueService.criar(dto);
    }
}