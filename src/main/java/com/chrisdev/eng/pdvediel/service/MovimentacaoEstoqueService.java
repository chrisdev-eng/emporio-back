package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.MovimentacaoEstoqueResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;
import com.chrisdev.eng.pdvediel.exception.EstoqueInsuficienteException;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    private static final Logger logger =
            LoggerFactory.getLogger(MovimentacaoEstoqueService.class);

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

        logger.info("Listando todas as movimentações de estoque");

        return movimentacaoEstoqueRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public MovimentacaoEstoqueResponseDTO buscarPorId(Long id) {

        logger.info("Buscando movimentação de estoque pelo ID {}", id);

        MovimentacaoEstoque movimentacao = movimentacaoEstoqueRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Movimentação não encontrada"));

        return converterParaResponse(movimentacao);
    }

    @Transactional
    public MovimentacaoEstoqueResponseDTO criar(MovimentacaoEstoqueRequestDTO dto) {

        logger.info(
                "Iniciando movimentação de estoque. Item: {}, tipo: {}, quantidade: {}, usuário: {}",
                dto.itemId(),
                dto.tipo(),
                dto.quantidade(),
                dto.usuarioId()
        );

        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Item não encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        Estoque estoque = estoqueRepository.findByItemId(item.getId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Estoque não encontrado para o item"));

        Integer quantidadeAnterior = estoque.getQuantidade();

        if (dto.tipo() == TipoMovimentacao.SAIDA) {

            if (estoque.getQuantidade() < dto.quantidade()) {

                logger.warn(
                        "Estoque insuficiente para o item {}. Disponível: {}, solicitado: {}",
                        item.getNome(),
                        estoque.getQuantidade(),
                        dto.quantidade()
                );

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

        logger.info(
                "Movimentação {} realizada com sucesso. Item: {}, estoque: {} -> {}",
                movimentacaoSalva.getId(),
                item.getNome(),
                quantidadeAnterior,
                estoque.getQuantidade()
        );

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
