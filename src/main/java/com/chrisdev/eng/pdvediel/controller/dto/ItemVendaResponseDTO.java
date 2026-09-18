package com.chrisdev.eng.pdvediel.controller.dto;

import java.math.BigDecimal;

public record ItemVendaResponseDTO(
        Long itemId,
        String itemNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}