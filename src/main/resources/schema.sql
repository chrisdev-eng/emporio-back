-- =========================================================
-- EMPÓRIO DAS CHAVES
-- Script oficial de criação do banco
-- PostgreSQL
-- =========================================================


-- =========================================================
-- 1. CATEGORIAS
-- =========================================================

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);


-- =========================================================
-- 2. USUARIOS
-- =========================================================

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);


-- =========================================================
-- 3. ITENS
-- =========================================================

CREATE TABLE itens (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    preco NUMERIC(19,2) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    categoria_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_itens_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id)
);


-- =========================================================
-- 4. VENDAS
-- =========================================================

CREATE TABLE vendas (
    id BIGSERIAL PRIMARY KEY,
    data TIMESTAMP NOT NULL,
    valor_total NUMERIC(19,2) NOT NULL,
    valor_recebido NUMERIC(19,2) NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_vendas_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);


-- =========================================================
-- 5. ESTOQUES
-- =========================================================

CREATE TABLE estoques (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL UNIQUE,
    quantidade INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_estoques_item
        FOREIGN KEY (item_id)
        REFERENCES itens(id)
);


-- =========================================================
-- 6. ITENS DA VENDA
-- =========================================================

CREATE TABLE itens_venda (
    id BIGSERIAL PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(19,2) NOT NULL,

    CONSTRAINT fk_itens_venda_venda
        FOREIGN KEY (venda_id)
        REFERENCES vendas(id),

    CONSTRAINT fk_itens_venda_item
        FOREIGN KEY (item_id)
        REFERENCES itens(id)
);


-- =========================================================
-- 7. MOVIMENTACOES DE ESTOQUE
-- =========================================================

CREATE TABLE movimentacoes_estoque (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    data TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_movimentacoes_estoque_item
        FOREIGN KEY (item_id)
        REFERENCES itens(id),

    CONSTRAINT fk_movimentacoes_estoque_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);