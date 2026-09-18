package com.chrisdev.eng.pdvediel.controller.dto;

public record EstoqueResponseDTO(
        Long id,
        Long itemId,
        String itemNome,
        Integer quantidade
) {
}