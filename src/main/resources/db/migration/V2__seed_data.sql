-- Usuário DONO
INSERT INTO usuarios (nome, login, senha, perfil, ativo)
VALUES ('Administrador', 'admin', 'admin123', 'DONO', TRUE)
    ON CONFLICT (login) DO NOTHING;

-- Usuário FUNCIONARIO
INSERT INTO usuarios (nome, login, senha, perfil, ativo)
VALUES ('Funcionario', 'funcionario', '123456', 'FUNCIONARIO', TRUE)
    ON CONFLICT (login) DO NOTHING;

-- Categoria inicial
INSERT INTO categorias (nome, ativo)
VALUES ('Chaves', TRUE)
    ON CONFLICT (nome) DO NOTHING;