package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VendaRequestDTO(

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamento formaPagamento,

        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        Long clienteId,

        @NotEmpty(message = "A venda deve possuir pelo menos um item")
        List<@Valid ItemVendaRequestDTO> itens

) {
}