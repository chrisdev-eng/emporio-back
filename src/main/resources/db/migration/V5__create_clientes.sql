CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    cep VARCHAR(8) NOT NULL,
    logradouro VARCHAR(255),
    numero VARCHAR(50),
    complemento VARCHAR(255),
    bairro VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(2),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE vendas
ADD COLUMN cliente_id BIGINT;

ALTER TABLE vendas
ADD CONSTRAINT fk_vendas_cliente
FOREIGN KEY (cliente_id)
REFERENCES clientes(id);