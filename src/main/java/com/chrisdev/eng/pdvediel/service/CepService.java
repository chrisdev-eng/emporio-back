package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.client.ViaCepClient;
import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

@Service
public class CepService {

    private final ViaCepClient viaCepClient;

    public CepService(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public ViaCepResponse buscarPorCep(String cep) {
        ViaCepResponse resposta = viaCepClient.buscarPorCep(cep);

        if (Boolean.TRUE.equals(resposta.erro())) {
            throw new RecursoNaoEncontradoException("CEP não encontrado");
        }

        return resposta;
    }
}