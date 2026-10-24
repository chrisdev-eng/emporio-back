package com.chrisdev.eng.pdvediel.controller;

import com.chrisdev.eng.pdvediel.base.BaseIntegrationTest;
import com.chrisdev.eng.pdvediel.entity.Usuario;
import com.chrisdev.eng.pdvediel.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//testes de integracao dos endpoints de usuario
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UsuarioControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveListarTodosOsUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].login").value("admin"));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        Usuario usuario = usuarioPorLogin("admin");

        mockMvc.perform(get("/usuarios/{id}", usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.perfil").value("DONO"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExiste() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveCriarUsuario() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Novo Usuario\", \"login\": \"novo\","
                                + "\"senha\": \"123456\", \"perfil\": \"FUNCIONARIO\", \"ativo\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("novo"))
                .andExpect(jsonPath("$.perfil").value("FUNCIONARIO"))
                .andExpect(jsonPath("$.ativo").value(true));

        assertThat(usuarioPorLogin("novo")).isNotNull();
    }

    @Test
    void deveRetornar400QuandoFaltarSenhaNoUsuario() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Sem Senha\", \"login\": \"semSenha\","
                                + "\"perfil\": \"FUNCIONARIO\", \"ativo\": true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar409QuandoLoginJaExiste() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Duplicado\", \"login\": \"admin\","
                                + "\"senha\": \"123456\", \"perfil\": \"DONO\", \"ativo\": true}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Conflito de dados"));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        Usuario usuario = usuarioPorLogin("joao");

        mockMvc.perform(put("/usuarios/{id}", usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Joao Atualizado\", \"login\": \"joao\","
                                + "\"perfil\": \"DONO\", \"ativo\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Joao Atualizado"))
                .andExpect(jsonPath("$.perfil").value("DONO"));
    }

    @Test
    void deveAtualizarUsuarioTrocandoASenha() throws Exception {
        Usuario usuario = usuarioPorLogin("maria");
        String senhaOriginal = usuario.getSenha();

        mockMvc.perform(put("/usuarios/{id}", usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Maria Souza\", \"login\": \"maria\","
                                + "\"senha\": \"novasenha\", \"perfil\": \"FUNCIONARIO\", \"ativo\": true}"))
                .andExpect(status().isOk());

        assertThat(usuarioRepository.findById(usuario.getId()).orElseThrow().getSenha())
                .isNotEqualTo(senhaOriginal);
    }

    @Test
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        mockMvc.perform(put("/usuarios/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Fantasma\", \"login\": \"fantasma\","
                                + "\"perfil\": \"DONO\", \"ativo\": true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveDesativarUsuarioAoExcluir() throws Exception {
        Usuario usuario = usuarioPorLogin("funcionario");

        mockMvc.perform(delete("/usuarios/{id}", usuario.getId()))
                .andExpect(status().isNoContent());

        assertThat(usuarioRepository.findById(usuario.getId()).orElseThrow().getAtivo()).isFalse();
    }

    @Test
    void deveRetornar404AoExcluirUsuarioInexistente() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private Usuario usuarioPorLogin(String login) {
        return usuarioRepository.findByLogin(login).orElseThrow();
    }
}
