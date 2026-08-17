package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//contem as regras de negocio relacionados as movimetnações de estoque
@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
    }

    public List<MovimentacaoEstoque> listarTodas() {
        return movimentacaoEstoqueRepository.findAll();
    }

    public MovimentacaoEstoque buscarPorId(Long id) {
        return movimentacaoEstoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
    }

    public MovimentacaoEstoque criar(MovimentacaoEstoque movimentacao) {
        return movimentacaoEstoqueRepository.save(movimentacao);
    }
}