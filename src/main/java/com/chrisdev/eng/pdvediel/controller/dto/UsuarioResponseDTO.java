package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.Perfil;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String login,
        Perfil perfil,
        Boolean ativo
) {
}