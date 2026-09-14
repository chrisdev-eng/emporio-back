package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.exception.CredenciaisInvalidasException;
import com.chrisdev.eng.pdvediel.controller.dto.LoginRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.LoginResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;

    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .orElseThrow(() ->
                        new CredenciaisInvalidasException("Login ou senha inválidos"));

        if (!usuario.getSenha().equals(request.senha())) {
            throw new CredenciaisInvalidasException("Login ou senha inválidos");
        }

        if (!usuario.getAtivo()) {
            throw new CredenciaisInvalidasException("Usuário inativo");
        }

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getPerfil()
        );
    }
}