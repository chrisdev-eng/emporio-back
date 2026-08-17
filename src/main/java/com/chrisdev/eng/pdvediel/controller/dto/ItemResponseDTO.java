package com.chrisdev.eng.pdvediel.controller.dto;

import com.chrisdev.eng.pdvediel.entity.TipoItem;

import java.math.BigDecimal;

//DTO utilizado pra representar os dados de um item nas resp da API
public record ItemResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        TipoItem tipo,
        Long categoriaId,
        String categoriaNome,
        Boolean ativo
) {
}