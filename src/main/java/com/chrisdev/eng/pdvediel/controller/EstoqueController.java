package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.EstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.EstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.service.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Controller responsável pelas op de consulta e gerenciamento de estoque
@RestController
@RequestMapping("/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public List<EstoqueResponseDTO> listarTodos() {
        return estoqueService.listarTodos();
    }

    @GetMapping("/{id}")
    public EstoqueResponseDTO buscarPorId(@PathVariable Long id) {
        return estoqueService.buscarPorId(id);
    }

    //cadastra um novo registro de estoque
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstoqueResponseDTO criar(
            @Valid @RequestBody EstoqueRequestDTO dto) {

        return estoqueService.criar(dto);
    }

    //att o registro de estoque pelo ID
    @PutMapping("/{id}")
    public EstoqueResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueRequestDTO dto) {

        return estoqueService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        estoqueService.excluir(id);
    }
}