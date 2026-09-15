package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import com.chrisdev.eng.pdvediel.service.CepService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ViaCepResponse buscarPorCep(@PathVariable String cep) {
        return cepService.buscarPorCep(cep);
    }
}