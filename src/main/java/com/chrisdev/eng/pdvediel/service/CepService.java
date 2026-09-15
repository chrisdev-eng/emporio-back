package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.client.ViaCepClient;
import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import org.springframework.stereotype.Service;

@Service
public class CepService {

    private final ViaCepClient viaCepClient;

    public CepService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public ViaCepResponse buscarPorCep(String cep) {
        return viaCepClient.buscarPorCep(cep);
    }
}