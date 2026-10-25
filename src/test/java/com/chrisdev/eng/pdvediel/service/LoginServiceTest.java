package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.LoginRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.LoginResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Perfil;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.exception.CredenciaisInvalidasException;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//testes unitarios da autenticacao dos usuarios
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @Test
    void deveAutenticarUsuarioComCredenciaisValidas() {
        Usuario usuario = usuario(true);
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("admin123", usuario.getSenha())).thenReturn(true);

        LoginResponseDTO resultado = loginService.autenticar(new LoginRequestDTO("admin", "admin123"));

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.login()).isEqualTo("admin");
        assertThat(resultado.nome()).isEqualTo("Administrador");
        assertThat(resultado.perfil()).isEqualTo(Perfil.DONO);
        verify(usuarioRepository).findByLogin("admin");
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        var requisicao = new LoginRequestDTO("inexistente", "admin123");

        assertThatThrownBy(() -> loginService.autenticar(requisicao))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Login ou senha inválidos");
    }

    @Test
    void deveLancarErroQuandoSenhaNaoConfere() {
        Usuario usuario = usuario(true);
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaerrada", usuario.getSenha())).thenReturn(false);

        var requisicao = new LoginRequestDTO("admin", "senhaerrada");

        assertThatThrownBy(() -> loginService.autenticar(requisicao))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Login ou senha inválidos");
    }

    @Test
    void deveLancarErroQuandoUsuarioEstaInativo() {
        Usuario usuario = usuario(false);
        when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("admin123", usuario.getSenha())).thenReturn(true);

        var requisicao = new LoginRequestDTO("admin", "admin123");

        assertThatThrownBy(() -> loginService.autenticar(requisicao))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessage("Usuário inativo");
    }

    private Usuario usuario(boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Administrador");
        usuario.setLogin("admin");
        usuario.setSenha("hash-cripografado");
        usuario.setPerfil(Perfil.DONO);
        usuario.setAtivo(ativo);
        return usuario;
    }
}
