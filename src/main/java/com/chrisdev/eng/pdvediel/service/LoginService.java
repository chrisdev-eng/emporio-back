package com.chrisdev.eng.pdvediel.service;

import com.chrisdev.eng.pdvediel.controller.dto.LoginRequestDTO;
import com.chrisdev.eng.pdvediel.controller.dto.LoginResponseDTO;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.exception.CredenciaisInvalidasException;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private static final Logger logger =
            LoggerFactory.getLogger(LoginService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO request) {

        logger.info("Tentativa de login para o usuário {}", request.login());

        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .orElseThrow(() -> {
                    logger.warn(
                            "Tentativa de login com usuário inexistente: {}",
                            request.login()
                    );

                    return new CredenciaisInvalidasException(
                            "Login ou senha inválidos"
                    );
                });

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            logger.warn(
                    "Tentativa de login com senha inválida para o usuário {}",
                    request.login()
            );

            throw new CredenciaisInvalidasException(
                    "Login ou senha inválidos"
            );
        }

        if (!usuario.getAtivo()) {
            logger.warn(
                    "Tentativa de login com usuário inativo: {}",
                    request.login()
            );

            throw new CredenciaisInvalidasException("Usuário inativo");
        }

        logger.info(
                "Login realizado com sucesso. Usuário: {}, perfil: {}",
                usuario.getLogin(),
                usuario.getPerfil()
        );

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getNome(),
                usuario.getPerfil()
        );
    }
}