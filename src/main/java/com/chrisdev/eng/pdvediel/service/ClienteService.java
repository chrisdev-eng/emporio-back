package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import com.chrisdev.eng.pdvediel.controller.dto.ClienteRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ClienteResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Cliente;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CepService cepService;

    public ClienteService(
            ClienteRepository clienteRepository,
            CepService cepService) {

        this.clienteRepository = clienteRepository;
        this.cepService = cepService;
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Cliente não encontrado"));

        return converterParaResponse(cliente);
    }

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {

        ViaCepResponse endereco = cepService.buscarPorCep(dto.cep());

        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setCep(dto.cep());
        cliente.setNumero(dto.numero());
        cliente.setComplemento(dto.complemento());

        cliente.setLogradouro(endereco.logradouro());
        cliente.setBairro(endereco.bairro());
        cliente.setCidade(endereco.localidade());
        cliente.setUf(endereco.uf());

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return converterParaResponse(clienteSalvo);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Cliente não encontrado"));

        ViaCepResponse endereco = cepService.buscarPorCep(dto.cep());

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setCep(dto.cep());
        cliente.setNumero(dto.numero());
        cliente.setComplemento(dto.complemento());

        cliente.setLogradouro(endereco.logradouro());
        cliente.setBairro(endereco.bairro());
        cliente.setCidade(endereco.localidade());
        cliente.setUf(endereco.uf());

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return converterParaResponse(clienteAtualizado);
    }

    public void excluir(Long id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Cliente não encontrado"));

        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    private ClienteResponseDTO converterParaResponse(Cliente cliente) {

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getCep(),
                cliente.getLogradouro(),
                cliente.getNumero(),
                cliente.getComplemento(),
                cliente.getBairro(),
                cliente.getCidade(),
                cliente.getUf(),
                cliente.getAtivo()
        );
    }
}