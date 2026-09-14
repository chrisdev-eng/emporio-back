package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O perfil é obrigatório")
        Perfil perfil,

        @NotNull(message = "O status ativo é obrigatório")
        Boolean ativo
) {
}