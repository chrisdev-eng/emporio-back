package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.UsuarioRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.UsuarioResponseDTO;
import com.chrisdev.eng.pdvediel.controller.dto.UsuarioUpdateDTO;
import com.chrisdev.eng.pdvediel.entity.Perfil;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.exception.RecursoNaoEncontradoException;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios das regras de negocio dos usuarios
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveListarTodosOsUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario(1L, "admin")));

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).login()).isEqualTo("admin");
        assertThat(resultado.get(0).perfil()).isEqualTo(Perfil.DONO);
        verify(usuarioRepository).findAll();
    }

    @Test
    void deveListarUsuariosQuandoNaoHaRegistro() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        assertThat(usuarioService.listarTodos()).isEmpty();
    }

    @Test
    void deveBuscarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "admin")));

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Administrador");
        assertThat(resultado.ativo()).isTrue();
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveCriarUsuarioComSenhaCriptografada() {
        when(passwordEncoder.encode("123456")).thenReturn("hash-cripografado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> {
            Usuario usuario = invocacao.getArgument(0);
            usuario.setId(10L);
            return usuario;
        });

        UsuarioResponseDTO resultado = usuarioService.criar(
                new UsuarioRequestDTO("Maria Souza", "maria", "123456", Perfil.FUNCIONARIO, true));

        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.login()).isEqualTo("maria");
        assertThat(resultado.perfil()).isEqualTo(Perfil.FUNCIONARIO);
        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveAtualizarUsuarioTrocandoASenha() {
        Usuario usuario = usuario(1L, "admin");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("hash-novo");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.atualizar(1L, new UsuarioUpdateDTO(
                "Novo Nome", "admin", "novaSenha", Perfil.DONO, true));

        assertThat(resultado.nome()).isEqualTo("Novo Nome");
        assertThat(usuario.getSenha()).isEqualTo("hash-novo");
        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    void deveManterASenhaQuandoNaoInformadaNaAtualizacao() {
        Usuario usuario = usuario(1L, "admin");
        String senhaOriginal = usuario.getSenha();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.atualizar(1L, new UsuarioUpdateDTO(
                "Novo Nome", "admin", null, Perfil.DONO, true));

        assertThat(usuario.getSenha()).isEqualTo(senhaOriginal);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveManterASenhaQuandoVemEmBrancoNaAtualizacao() {
        Usuario usuario = usuario(1L, "admin");
        String senhaOriginal = usuario.getSenha();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.atualizar(1L, new UsuarioUpdateDTO(
                "Novo Nome", "admin", "   ", Perfil.DONO, true));

        assertThat(usuario.getSenha()).isEqualTo(senhaOriginal);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveLancarErroAoAtualizarUsuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        var requisicao = new UsuarioUpdateDTO(
                "Novo Nome", "admin", null, Perfil.DONO, true);

        assertThatThrownBy(() -> usuarioService.atualizar(99L, requisicao))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void deveDesativarUsuarioAoExcluir() {
        Usuario usuario = usuario(1L, "admin");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.excluir(1L);

        assertThat(usuario.getAtivo()).isFalse();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarErroAoExcluirUsuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.excluir(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }

    private Usuario usuario(Long id, String login) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Administrador");
        usuario.setLogin(login);
        usuario.setSenha("hash-cripografado");
        usuario.setPerfil(Perfil.DONO);
        usuario.setAtivo(true);
        return usuario;
    }
}
