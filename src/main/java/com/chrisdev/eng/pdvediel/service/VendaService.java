package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.ItemVendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ItemVendaResponseDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.VendaResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Cliente;
import com.chrisdev.eng.pdvediel.entity.Estoque;
import com.chrisdev.eng.pdvediel.entity.Item;
import com.chrisdev.eng.pdvediel.entity.ItemVenda;
import com.chrisdev.eng.pdvediel.entity.MovimentacaoEstoque;
import com.chrisdev.eng.pdvediel.entity.TipoItem;
import com.chrisdev.eng.pdvediel.entity.TipoMovimentacao;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.entity.Venda;
import com.chrisdev.eng.pdvediel.exception.EstoqueInsuficienteException;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.exception.ValorRecebidoInsuficienteException;
import com.chrisdev.eng.pdvediel.repository.ClienteRepository;
import com.chrisdev.eng.pdvediel.repository.EstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.ItemRepository;
import com.chrisdev.eng.pdvediel.repository.ItemVendaRepository;
import com.chrisdev.eng.pdvediel.repository.MovimentacaoEstoqueRepository;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import com.chrisdev.eng.pdvediel.repository.VendaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    private static final Logger logger = LoggerFactory.getLogger(VendaService.class);

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final ItemRepository itemRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ClienteRepository clienteRepository;

    public VendaService(
            VendaRepository vendaRepository,
            ItemVendaRepository itemVendaRepository,
            ItemRepository itemRepository,
            EstoqueRepository estoqueRepository,
            UsuarioRepository usuarioRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            ClienteRepository clienteRepository) {

        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.itemRepository = itemRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public VendaResponseDTO criar(VendaRequestDTO dto) {

        logger.info("Iniciando venda para o usuário {}", dto.usuarioId());
        logger.info("Valor recebido informado: {}", dto.valorRecebido());

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!usuario.getAtivo()) {
            throw new RecursoNaoEncontradoException("Usuário inativo");
        }

        Cliente cliente = null;

        if (dto.clienteId() != null) {
            cliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));

            if (!cliente.getAtivo()) {
                throw new RecursoNaoEncontradoException("Cliente inativo");
            }
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        List<ItemVenda> itensVenda = new ArrayList<>();

        for (ItemVendaRequestDTO itemDTO : dto.itens()) {

            Item item = itemRepository.findById(itemDTO.itemId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado"));

            if (!item.getAtivo()) {
                throw new RecursoNaoEncontradoException("Item inativo");
            }

            if (item.getTipo() == TipoItem.PRODUTO) {

                Estoque estoque = estoqueRepository.findByItemId(item.getId())
                        .orElseThrow(() -> new RecursoNaoEncontradoException(
                                "Estoque não encontrado para o produto"));

                if (estoque.getQuantidade() < itemDTO.quantidade()) {

                    logger.warn(
                            "Estoque insuficiente para o item {}. Disponível: {}, solicitado: {}",
                            item.getNome(),
                            estoque.getQuantidade(),
                            itemDTO.quantidade());

                    throw new EstoqueInsuficienteException(
                            "Estoque insuficiente para o item: " + item.getNome());
                }

                estoque.setQuantidade(
                        estoque.getQuantidade() - itemDTO.quantidade());

                estoqueRepository.save(estoque);

                MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
                movimentacao.setItem(item);
                movimentacao.setQuantidade(itemDTO.quantidade());
                movimentacao.setTipo(TipoMovimentacao.SAIDA);
                movimentacao.setData(LocalDateTime.now());
                movimentacao.setUsuario(usuario);

                movimentacaoEstoqueRepository.save(movimentacao);
            }

            BigDecimal precoUnitario = item.getPreco();

            BigDecimal subtotal = precoUnitario.multiply(
                    BigDecimal.valueOf(itemDTO.quantidade()));

            valorTotal = valorTotal.add(subtotal);

            logger.info(
                    "Item {} adicionado à venda. Quantidade: {}, subtotal: {}",
                    item.getNome(),
                    itemDTO.quantidade(),
                    subtotal);

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setItem(item);
            itemVenda.setQuantidade(itemDTO.quantidade());
            itemVenda.setPrecoUnitario(precoUnitario);

            itensVenda.add(itemVenda);
        }

        if (dto.valorRecebido().compareTo(valorTotal) < 0) {
            throw new ValorRecebidoInsuficienteException(
                    "O valor recebido é menor que o valor total da venda");
        }

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setValorTotal(valorTotal);
        venda.setValorRecebido(dto.valorRecebido());
        venda.setFormaPagamento(dto.formaPagamento());
        venda.setUsuario(usuario);
        venda.setCliente(cliente);

        Venda vendaSalva = vendaRepository.save(venda);

        for (ItemVenda itemVenda : itensVenda) {
            itemVenda.setVenda(vendaSalva);
            itemVendaRepository.save(itemVenda);
        }

        logger.info(
                "Venda {} realizada com sucesso. Usuário: {}, valor total: {}, forma de pagamento: {}",
                vendaSalva.getId(),
                usuario.getLogin(),
                valorTotal,
                dto.formaPagamento());

        return converterParaResponse(vendaSalva);
    }

    public List<VendaResponseDTO> listarTodas() {

        logger.info("Listando todas as vendas");

        return vendaRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public VendaResponseDTO buscarPorId(Long id) {

        logger.info("Buscando venda pelo ID {}", id);

        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada"));

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
                            subtotal);
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
                venda.getCliente() != null ? venda.getCliente().getId() : null,
                venda.getCliente() != null ? venda.getCliente().getNome() : null,
                itens);
    }
}