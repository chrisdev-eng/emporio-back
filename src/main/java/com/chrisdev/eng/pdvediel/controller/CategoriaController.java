package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.CategoriaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.CategoriaResponseDTO;
import com.chrisdev.eng.pdvediel.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//Controller responsável pelas op relacionadas á categoria.
//Disponilibiza endpoints pra cadastro, consulta, atualização e exclusão
@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {

        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    //cadastra nova categoria e retorna o recurso que foi criado
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(
           @Valid @RequestBody CategoriaRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoriaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id,
           @Valid @RequestBody CategoriaRequestDTO dto) {

        return ResponseEntity.ok(
                categoriaService.atualizar(id, dto)
        );
    }

    //exclui a categoria informada e retorna um 204 quando foi finalizado a operacao
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        categoriaService.excluir(id);

        return ResponseEntity.noContent().build();
    }

    //aqui busca categoria pelo nome informado como o parametroda req
    @GetMapping("/buscar")
    public ResponseEntity<List<CategoriaResponseDTO>> buscarPorNome(
            @RequestParam String nome) {

        return ResponseEntity.ok(
                categoriaService.buscarPorNome(nome)
        );
    }
}