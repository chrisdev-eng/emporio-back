package com.chrisdev.eng.pdvediel.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "viaCepClient",
        url = "https://viacep.com.br/ws"
)
public interface ViaCepClient {

    @GetMapping("/{cep}/json")
    ViaCepResponse buscarPorCep(@PathVariable String cep);
}