package com.chrisdev.eng.pdvediel.controller.dto;

//DTO utilizado pra representar os dados de uma categorias na resposta da API
public record CategoriaResponseDTO(
        Long id,
        String nome,
        Boolean ativo
) {
}