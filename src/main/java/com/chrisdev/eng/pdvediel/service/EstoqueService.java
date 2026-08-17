package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//contem as regras de negocio relacionadas ao controle de estoque
@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }

    public Estoque buscarPorId(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
    }

    public Estoque criar(Estoque estoque) {
        return estoqueRepository.save(estoque);
    }

    public Estoque atualizar(Long id, Estoque estoque) {
        Estoque estoqueExistente = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        estoqueExistente.setItem(estoque.getItem());
        estoqueExistente.setQuantidade(estoque.getQuantidade());

        return estoqueRepository.save(estoqueExistente);
    }

    public void excluir(Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));

        estoqueRepository.delete(estoque);
    }
}