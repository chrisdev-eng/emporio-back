package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        LocalDateTime data,
        BigDecimal valorTotal,
        BigDecimal valorRecebido,
        FormaPagamento formaPagamento,
        Long usuarioId,
        String usuarioNome,
        List<ItemVendaResponseDTO> itens
) {
}