package com.chrisdev.eng.pdvediel.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EstoqueRequestDTO(

        @NotNull(message = "O item é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantidade

) {
}