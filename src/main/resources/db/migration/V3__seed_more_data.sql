-- =====================================================
-- DADOS INICIAIS ADICIONAIS PARA TESTES
-- =====================================================

-- Categorias adicionais
INSERT INTO categorias (nome, ativo)
VALUES
    ('Cadeados', TRUE),
    ('Acessorios', TRUE),
    ('Servicos', TRUE)
    ON CONFLICT (nome) DO NOTHING;


-- Usuarios adicionais
INSERT INTO usuarios (nome, login, senha, perfil, ativo)
VALUES
    ('Joao Silva', 'joao', '123456', 'FUNCIONARIO', TRUE),
    ('Maria Souza', 'maria', '123456', 'FUNCIONARIO', TRUE)
    ON CONFLICT (login) DO NOTHING;


-- Produtos
INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo)
SELECT
    'Chave Yale',
    'Chave virgem modelo Yale',
    8.00,
    'PRODUTO',
    c.id,
    TRUE
FROM categorias c
WHERE c.nome = 'Chaves'
  AND NOT EXISTS (
    SELECT 1
    FROM itens i
    WHERE i.nome = 'Chave Yale'
);

INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo)
SELECT
    'Chave Tetra',
    'Chave virgem modelo Tetra',
    15.00,
    'PRODUTO',
    c.id,
    TRUE
FROM categorias c
WHERE c.nome = 'Chaves'
  AND NOT EXISTS (
    SELECT 1
    FROM itens i
    WHERE i.nome = 'Chave Tetra'
);

INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo)
SELECT
    'Cadeado 30mm',
    'Cadeado de 30 milimetros',
    35.00,
    'PRODUTO',
    c.id,
    TRUE
FROM categorias c
WHERE c.nome = 'Cadeados'
  AND NOT EXISTS (
    SELECT 1
    FROM itens i
    WHERE i.nome = 'Cadeado 30mm'
);

INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo)
SELECT
    'Chaveiro Argola',
    'Chaveiro simples com argola metalica',
    5.00,
    'PRODUTO',
    c.id,
    TRUE
FROM categorias c
WHERE c.nome = 'Acessorios'
  AND NOT EXISTS (
    SELECT 1
    FROM itens i
    WHERE i.nome = 'Chaveiro Argola'
);


-- Servicos
INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo)
SELECT
    'Copia de Chave',
    'Servico de copia de chave comum',
    10.00,
    'SERVICO',
    c.id,
    TRUE
FROM categorias c
WHERE c.nome = 'Servicos'
  AND NOT EXISTS (
    SELECT 1
    FROM itens i
    WHERE i.nome = 'Copia de Chave'
);

INSERT INTO itens (nome, descricao, preco, tipo, categoria_id, ativo)
SELECT
    'Abertura de Fechadura',
    'Servico de abertura de fechadura',
    80.00,
    'SERVICO',
    c.id,
    TRUE
FROM categorias c
WHERE c.nome = 'Servicos'
  AND NOT EXISTS (
    SELECT 1
    FROM itens i
    WHERE i.nome = 'Abertura de Fechadura'
);


-- Estoque apenas dos produtos
INSERT INTO estoques (item_id, quantidade)
SELECT i.id, 50
FROM itens i
WHERE i.nome = 'Chave Yale'
  AND NOT EXISTS (
    SELECT 1
    FROM estoques e
    WHERE e.item_id = i.id
);

INSERT INTO estoques (item_id, quantidade)
SELECT i.id, 30
FROM itens i
WHERE i.nome = 'Chave Tetra'
  AND NOT EXISTS (
    SELECT 1
    FROM estoques e
    WHERE e.item_id = i.id
);

INSERT INTO estoques (item_id, quantidade)
SELECT i.id, 15
FROM itens i
WHERE i.nome = 'Cadeado 30mm'
  AND NOT EXISTS (
    SELECT 1
    FROM estoques e
    WHERE e.item_id = i.id
);

INSERT INTO estoques (item_id, quantidade)
SELECT i.id, 40
FROM itens i
WHERE i.nome = 'Chaveiro Argola'
  AND NOT EXISTS (
    SELECT 1
    FROM estoques e
    WHERE e.item_id = i.id
);