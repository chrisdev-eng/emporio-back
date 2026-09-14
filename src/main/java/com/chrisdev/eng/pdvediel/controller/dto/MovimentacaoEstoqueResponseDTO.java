package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;

import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponseDTO(
        Long id,
        Long itemId,
        String itemNome,
        Integer quantidade,
        TipoMovimentacao tipo,
        LocalDateTime data,
        Long usuarioId,
        String usuarioNome
) {
}