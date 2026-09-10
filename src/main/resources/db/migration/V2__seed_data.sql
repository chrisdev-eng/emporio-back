-- Usuário DONO
INSERT INTO usuarios (nome, login, senha, perfil, ativo)
VALUES ('Administrador', 'admin', 'admin123', 'DONO', TRUE);

-- Usuário FUNCIONARIO
INSERT INTO usuarios (nome, login, senha, perfil, ativo)
VALUES ('Funcionario', 'funcionario', '123456', 'FUNCIONARIO', TRUE);

-- Categoria inicial
INSERT INTO categorias (nome, ativo)
VALUES ('Chaves', TRUE);