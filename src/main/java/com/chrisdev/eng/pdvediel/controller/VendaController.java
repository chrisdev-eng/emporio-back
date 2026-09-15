package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.controller.dto.VendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaResponseDTO;
import com.chrisdev.eng.pdvediel.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "true"
)
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public List<VendaResponseDTO> listarTodas() {
        return vendaService.listarTodas();
    }

    @GetMapping("/{id}")
    public VendaResponseDTO buscarPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VendaResponseDTO criar(
            @Valid @RequestBody VendaRequestDTO dto) {

        return vendaService.criar(dto);
    }
}