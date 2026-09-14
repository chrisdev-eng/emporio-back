package com.chrisdev.eng.pdvediel.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemVendaRequestDTO(

        @NotNull(message = "O item é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser maior que zero")
        Integer quantidade

) {
}