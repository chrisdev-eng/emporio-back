package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;
import com.chrisdev.eng.pdvediel.exception.EstoqueInsuficienteException;

import java.time.LocalDateTime;
import java.util.List;

//contem as regras de negocio relacionados as movimetnações de estoque
@Service
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final EstoqueRepository estoqueRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            EstoqueRepository estoqueRepository,
            ItemRepository itemRepository,
            UsuarioRepository usuarioRepository) {

        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.estoqueRepository = estoqueRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<MovimentacaoEstoqueResponseDTO> listarTodas() {
        return movimentacaoEstoqueRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public MovimentacaoEstoqueResponseDTO buscarPorId(Long id) {
        MovimentacaoEstoque movimentacao = movimentacaoEstoqueRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Movimentação não encontrada"));

        return converterParaResponse(movimentacao);
    }

    @Transactional
    public MovimentacaoEstoqueResponseDTO criar(MovimentacaoEstoqueRequestDTO dto) {

        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Item não encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        Estoque estoque = estoqueRepository.findByItemId(item.getId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Estoque não encontrado para o item"));

        if (dto.tipo() == TipoMovimentacao.SAIDA) {
            if (estoque.getQuantidade() < dto.quantidade()) {
                throw new EstoqueInsuficienteException("Estoque insuficiente");
            }

            estoque.setQuantidade(
                    estoque.getQuantidade() - dto.quantidade()
            );
        } else {
            estoque.setQuantidade(
                    estoque.getQuantidade() + dto.quantidade()
            );
        }

        estoqueRepository.save(estoque);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setItem(item);
        movimentacao.setQuantidade(dto.quantidade());
        movimentacao.setTipo(dto.tipo());
        movimentacao.setData(LocalDateTime.now());
        movimentacao.setUsuario(usuario);

        MovimentacaoEstoque movimentacaoSalva =
                movimentacaoEstoqueRepository.save(movimentacao);

        return converterParaResponse(movimentacaoSalva);
    }

    private MovimentacaoEstoqueResponseDTO converterParaResponse(
            MovimentacaoEstoque movimentacao) {

        return new MovimentacaoEstoqueResponseDTO(
                movimentacao.getId(),
                movimentacao.getItem().getId(),
                movimentacao.getItem().getNome(),
                movimentacao.getQuantidade(),
                movimentacao.getTipo(),
                movimentacao.getData(),
                movimentacao.getUsuario().getId(),
                movimentacao.getUsuario().getNome()
        );
    }
}