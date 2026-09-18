package com.chrisdev.eng.pdvediel.controller.dto;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        Boolean ativo
) {
}