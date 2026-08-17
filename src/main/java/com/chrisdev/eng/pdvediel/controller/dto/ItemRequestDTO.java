package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.TipoItem;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

//DTO utilizado pra receber os dados necessários pra criação e att de um item
public record ItemRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.00", message = "O preço não pode ser negativo")
        BigDecimal preco,

        @NotNull(message = "O tipo é obrigatório")
        TipoItem tipo,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId

) {
}