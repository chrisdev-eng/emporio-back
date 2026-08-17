package com.chrisdev.eng.pdvediel.controller.dto;

import jakarta.validation.constraints.NotBlank;

//DTO utilizado para receber os dados necessários pra criação e att de uma categoria
public record CategoriaRequestDTO(

        @NotBlank(message = "O nome da categoria é obrigatório")
        String nome

) {
}