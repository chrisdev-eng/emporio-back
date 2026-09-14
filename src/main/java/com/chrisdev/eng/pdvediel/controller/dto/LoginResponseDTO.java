package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.Perfil;


public record LoginResponseDTO(
        Long id,
        String login,
        String nome,
        Perfil perfil
) {}