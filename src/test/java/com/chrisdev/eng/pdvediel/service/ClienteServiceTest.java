package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.client.ViaCepResponse;
import com.chrisdev.eng.pdvediel.controller.dto.ClienteRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.ClienteResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Cliente;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios das regras de negocio dos clientes
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CepService cepService;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deveListarTodosOsClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente(1L)));

        List<ClienteResponseDTO> resultado = clienteService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Cliente Teste");
        assertThat(resultado.get(0).cidade()).isEqualTo("São Paulo");
        verify(clienteRepository).findAll();
    }

    @Test
    void deveListarClientesQuandoNaoHaRegistro() {
        when(clienteRepository.findAll()).thenReturn(List.of());

        assertThat(clienteService.listarTodos()).isEmpty();
    }

    @Test
    void deveBuscarClientePorId() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente(1L)));

        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.cpf()).isEqualTo("12345678901");
        assertThat(resultado.uf()).isEqualTo("SP");
    }

    @Test
    void deveLancarErroQuandoClienteNaoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    void deveCriarClienteBuscandoOEnderecoPeloCep() {
        when(cepService.buscarPorCep("01001000")).thenReturn(endereco());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacao -> {
            Cliente cliente = invocacao.getArgument(0);
            cliente.setId(10L);
            return cliente;
        });

        ClienteResponseDTO resultado = clienteService.criar(
                new ClienteRequestDTO("Cliente Teste", "12345678901", "01001000", "100", "Apto 21"));

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.logradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.bairro()).isEqualTo("Sé");
        assertThat(resultado.cidade()).isEqualTo("São Paulo");
        assertThat(resultado.uf()).isEqualTo("SP");
        assertThat(resultado.numero()).isEqualTo("100");
        assertThat(resultado.complemento()).isEqualTo("Apto 21");
        verify(cepService).buscarPorCep("01001000");
    }

    @Test
    void deveAtualizarCliente() {
        Cliente cliente = cliente(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cepService.buscarPorCep("01001000")).thenReturn(endereco());
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        ClienteResponseDTO resultado = clienteService.atualizar(
                1L, new ClienteRequestDTO("Outro Nome", "12345678901", "01001000", "200", null));

        assertThat(resultado.nome()).isEqualTo("Outro Nome");
        assertThat(resultado.numero()).isEqualTo("200");
        assertThat(cliente.getNome()).isEqualTo("Outro Nome");
    }

    @Test
    void deveLancarErroAoAtualizarClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        var requisicao = new ClienteRequestDTO("Cliente Teste", "12345678901", "01001000", "100", null);

        assertThatThrownBy(() -> clienteService.atualizar(99L, requisicao))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    void deveDesativarClienteAoExcluir() {
        Cliente cliente = cliente(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        clienteService.excluir(1L);

        assertThat(cliente.getAtivo()).isFalse();
        verify(clienteRepository).save(cliente);
    }

    @Test
    void deveLancarErroAoExcluirClienteInexistente() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cliente não encontrado");
    }

    private ViaCepResponse endereco() {
        return new ViaCepResponse(
                "01001-000",
                "Praça da Sé",
                "lado ímpar",
                "unidade",
                "Sé",
                "São Paulo",
                "SP",
                "São Paulo",
                "Sudeste",
                "3550308",
                "1004",
                "11",
                "7107",
                false
        );
    }

    private Cliente cliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678901");
        cliente.setCep("01001000");
        cliente.setLogradouro("Praça da Sé");
        cliente.setNumero("100");
        cliente.setBairro("Sé");
        cliente.setCidade("São Paulo");
        cliente.setUf("SP");
        cliente.setAtivo(true);
        return cliente;
    }
}
