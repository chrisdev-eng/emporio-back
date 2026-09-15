package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.ItemVendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ItemVendaResponseDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.ItemVenda;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.entity.Venda;
import com.chrisdev.eng.pdvediel.exception.EstoqueInsuficienteException;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.ItemVendaRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import com.chrisdev.eng.pdvediel.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final ItemRepository itemRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;

    public VendaService(
            VendaRepository vendaRepository,
            ItemVendaRepository itemVendaRepository,
            ItemRepository itemRepository,
            EstoqueRepository estoqueRepository,
            UsuarioRepository usuarioRepository) {

        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.itemRepository = itemRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public VendaResponseDTO criar(VendaRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado"));

        BigDecimal valorTotal = BigDecimal.ZERO;

        List<ItemVenda> itensVenda = new ArrayList<>();

        for (ItemVendaRequestDTO itemDTO : dto.itens()) {

            Item item = itemRepository.findById(itemDTO.itemId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException("Item não encontrado"));

            Estoque estoque = estoqueRepository.findByItemId(item.getId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Estoque não encontrado para o item"));

            if (estoque.getQuantidade() < itemDTO.quantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o item: " + item.getNome()
                );
            }

            BigDecimal precoUnitario = item.getPreco();

            BigDecimal subtotal = precoUnitario.multiply(
                    BigDecimal.valueOf(itemDTO.quantidade())
            );

            valorTotal = valorTotal.add(subtotal);

            estoque.setQuantidade(
                    estoque.getQuantidade() - itemDTO.quantidade()
            );

            estoqueRepository.save(estoque);

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setItem(item);
            itemVenda.setQuantidade(itemDTO.quantidade());
            itemVenda.setPrecoUnitario(precoUnitario);

            itensVenda.add(itemVenda);
        }

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setValorTotal(valorTotal);
        venda.setValorRecebido(valorTotal);
        venda.setFormaPagamento(dto.formaPagamento());
        venda.setUsuario(usuario);

        Venda vendaSalva = vendaRepository.save(venda);

        for (ItemVenda itemVenda : itensVenda) {
            itemVenda.setVenda(vendaSalva);
            itemVendaRepository.save(itemVenda);
        }

        return converterParaResponse(vendaSalva);
    }

    public List<VendaResponseDTO> listarTodas() {
        return vendaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Venda não encontrada"));

        return converterParaResponse(venda);
    }

    private VendaResponseDTO converterParaResponse(Venda venda) {

        List<ItemVendaResponseDTO> itens = itemVendaRepository
                .findByVendaId(venda.getId())
                .stream()
                .map(itemVenda -> {

                    BigDecimal subtotal = itemVenda.getPrecoUnitario()
                            .multiply(BigDecimal.valueOf(itemVenda.getQuantidade()));

                    return new ItemVendaResponseDTO(
                            itemVenda.getItem().getId(),
                            itemVenda.getItem().getNome(),
                            itemVenda.getQuantidade(),
                            itemVenda.getPrecoUnitario(),
                            subtotal
                    );
                })
                .toList();

        return new VendaResponseDTO(
                venda.getId(),
                venda.getData(),
                venda.getValorTotal(),
                venda.getValorRecebido(),
                venda.getFormaPagamento(),
                venda.getUsuario().getId(),
                venda.getUsuario().getNome(),
                itens
        );
    }
}
