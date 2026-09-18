package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoEstoqueRequestDTO(

        @NotNull(message = "O item é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade,

        @NotNull(message = "O tipo de movimentação é obrigatório")
        TipoMovimentacao tipo,

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId

) {
}