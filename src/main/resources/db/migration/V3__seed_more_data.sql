-- =====================================================
-- DADOS INICIAIS ADICIONAIS PARA TESTES
-- =====================================================

-- Categorias adicionais
INSERT INTO categorias (nome, ativo) VALUES
('Cadeados', TRUE),
('Acessorios', TRUE),
('Servicos', TRUE);


-- Usuarios adicionais
INSERT INTO usuarios (nome, login, senha, perfil, ativo) VALUES
('Joao Silva', 'joao', '123456', 'FUNCIONARIO', TRUE),
('Maria Souza', 'maria', '123456', 'FUNCIONARIO', TRUE);


-- Produtos
INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo) VALUES
(
    'Chave Yale',
    'Chave virgem modelo Yale',
    8.00,
    'PRODUTO',
    (SELECT id FROM categorias WHERE nome = 'Chaves'),
    TRUE
),
(
    'Chave Tetra',
    'Chave virgem modelo Tetra',
    15.00,
    'PRODUTO',
    (SELECT id FROM categorias WHERE nome = 'Chaves'),
    TRUE
),
(
    'Cadeado 30mm',
    'Cadeado de 30 milimetros',
    35.00,
    'PRODUTO',
    (SELECT id FROM categorias WHERE nome = 'Cadeados'),
    TRUE
),
(
    'Chaveiro Argola',
    'Chaveiro simples com argola metalica',
    5.00,
    'PRODUTO',
    (SELECT id FROM categorias WHERE nome = 'Acessorios'),
    TRUE
);


-- Servicos
INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo) VALUES
(
    'Copia de Chave',
    'Servico de copia de chave comum',
    10.00,
    'SERVICO',
    (SELECT id FROM categorias WHERE nome = 'Servicos'),
    TRUE
),
(
    'Abertura de Fechadura',
    'Servico de abertura de fechadura',
    80.00,
    'SERVICO',
    (SELECT id FROM categorias WHERE nome = 'Servicos'),
    TRUE
);


-- Estoque apenas dos produtos
INSERT INTO estoques (item_id, quantidade) VALUES
((SELECT id FROM itens WHERE nome = 'Chave Yale'), 50),
((SELECT id FROM itens WHERE nome = 'Chave Tetra'), 30),
((SELECT id FROM itens WHERE nome = 'Cadeado 30mm'), 15),
((SELECT id FROM itens WHERE nome = 'Chaveiro Argola'), 40);