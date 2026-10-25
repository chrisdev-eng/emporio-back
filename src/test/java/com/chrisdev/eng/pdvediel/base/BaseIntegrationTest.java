package com.chrisdev.eng.pdvediel.base;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

//base para testes de integracao, usa um postgres embarcado (banco de teste)
@SpringBootTest
@Transactional
public abstract class BaseIntegrationTest {

    private static final EmbeddedPostgres POSTGRES = iniciarBanco();

    private static EmbeddedPostgres iniciarBanco() {
        try {
            return EmbeddedPostgres.builder().start();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao iniciar o Postgres embarcado", e);
        }
    }

    @DynamicPropertySource
    static void configurarBanco(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> "jdbc:postgresql://localhost:" + POSTGRES.getPort() + "/postgres");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }
}