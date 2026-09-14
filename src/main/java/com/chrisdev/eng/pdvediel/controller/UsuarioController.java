package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.UsuarioRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.UsuarioResponseDTO;
import com.chrisdev.eng.pdvediel.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//controller resp pela op de gerenciamento dos users
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criar(
            @Valid @RequestBody UsuarioRequestDTO dto) {

        return usuarioService.criar(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {

        return usuarioService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }
}