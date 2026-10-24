# PDV - Empório das Chaves

Sistema de Ponto de Venda (PDV) desenvolvido para o projeto da disciplina de Engenharia de Software/ADS da universidade Uniamérica.

O sistema tem como objetivo auxiliar no gerenciamento de produtos, serviços, categorias, usuários, estoque e vendas de uma chavearia.

======

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Gradle
- Lombok
- Docker
- Insomnia
- Git e GitHub

======

# Como executar o projeto

## Pré-requisitos

Para executar o projeto, é necessário possuir:

- Java 17
- Docker
- Git

O banco de dados utilizado pela aplicação é o PostgreSQL.

======

# Estrutura do projeto

O projeto utiliza uma arquitetura baseada no padrão MVC, separando as responsabilidades da aplicação em diferentes camadas.

## Controller

A camada controller é responsável por receber as requisições HTTP e disponibilizar os endpoints da API.

Controllers atualmente implementados:

CategoriaController
ItemController
UsuarioController
EstoqueController
MovimentacaoEstoqueController

## Service

A camada service concentra as regras de negócio da aplicação e faz a comunicação entre os controllers e repositories.

Services atualmente implementados:

CategoriaService
ItemService
UsuarioService
EstoqueService
MovimentacaoEstoqueService

## Repository

A camada repository é responsável pelo acesso aos dados utilizando Spring Data JPA.

Repositories atualmente implementados:

CategoriaRepository
ItemRepository
UsuarioRepository
EstoqueRepository
MovimentacaoEstoqueRepository

## Entity

A camada entity representa as entidades persistidas no banco de dados.

Entidades atualmente existentes:

Categoria
Item
Estoque
MovimentacaoEstoque
Usuario
Venda
ItemVenda


O projeto também possui os seguintes enums:


FormaPagamento
Perfil
TipoItem
TipoMovimentacao

## DTO

Os Data Transfer Objects (DTOs) são utilizados para controlar os dados enviados e recebidos pela API, evitando a exposição direta das entidades em determinadas operações.

DTOs atualmente implementados:


CategoriaRequestDTO
CategoriaResponseDTO
ItemRequestDTO
ItemResponseDTO

## Exception

A camada exception é responsável pelo tratamento de exceções específicas da aplicação.

Atualmente possui:

RecursoNaoEncontradoException
GlobalExceptionHandler

======

# Funcionalidades implementadas

## Categorias

A entidade Categoria possui CRUD completo.

Endpoints:

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/categorias` | Lista todas as categorias |
| GET | `/categorias/{id}` | Busca uma categoria pelo ID |
| GET | `/categorias/buscar?nome=` | Busca categorias pelo nome |
| POST | `/categorias` | Cria uma categoria |
| PUT | `/categorias/{id}` | Atualiza uma categoria |
| DELETE | `/categorias/{id}` | Exclui uma categoria |

A implementação utiliza:

- Controller
- Service
- Repository
- Entity
- DTOs
- Validação
- Tratamento de exceções
- Códigos HTTP apropriados

======

## Itens

A entidade Item possui operações para:

- Listagem
- Busca por ID
- Busca por nome
- Cadastro
- Atualização
- Exclusão

Cada item possui uma categoria associada.

======

## Usuários

A entidade Usuario possui operações básicas para:

- Listagem
- Busca por ID
- Cadastro
- Atualização
- Exclusão

Os usuários possuem um perfil que permite diferenciar:

- DONO
- FUNCIONARIO

======

## Estoque

A entidade Estoque possui operações básicas para:

- Listagem
- Busca por ID
- Cadastro
- Atualização
- Exclusão

Cada registro de estoque está associado a um item.

======

## Movimentações de estoque

A entidade MovimentacaoEstoque possui atualmente operações para:

- Listagem
- Busca por ID
- Cadastro

As movimentações possuem os tipos:

- ENTRADA
- SAIDA

======

# Banco de dados

O projeto utiliza PostgreSQL com JPA/Hibernate.

Atualmente, a aplicação possui as seguintes tabelas:

categorias
itens
estoques
movimentacoes_estoque
usuarios
vendas
itens_venda


Principais relacionamentos:

Categoria
    -> Item

Item
    -> Estoque
    -> MovimentacaoEstoque
    -> ItemVenda

Usuario
    -> MovimentacaoEstoque
    -> Venda

Venda -> ItemVenda


Os relacionamentos são definidos através das anotações JPA nas entidades.

======

# Testes da API

Os endpoints da aplicação são testados utilizando o Insomnia.

Durante o desenvolvimento são realizados testes de:

- Requisições GET
- Requisições POST
- Requisições PUT
- Requisições DELETE
- Validação dos dados enviados
- Códigos HTTP retornados
- Persistência dos dados no PostgreSQL

======

# Testes automatizados e cobertura (JaCoCo)

Além dos testes manuais com o Insomnia, o projeto possui uma suíte de testes automatizados com **JUnit 5**, **Mockito** e **MockMvc**, dividida em dois tipos:

| Tipo | Padrão de nome | O que cobre | Ferramentas |
|---|---|---|---|
| Testes unitários | `*Test.java` | Regras de negócio de cada service, isoladas com mocks dos repositories | JUnit 5 + Mockito |
| Testes de integração | `*IT.java` | Endpoints HTTP completos, do controller até o banco | Spring Boot Test + MockMvc + PostgreSQL embarcado |

Os testes de integração sobem um **PostgreSQL embarcado** (`io.zonky.test:embedded-postgres`) em uma porta aleatória, aplicam as migrations do Flyway e rodam cada teste dentro de uma transação que é revertida ao final. Não é necessário ter Docker, PostgreSQL instalado ou qualquer banco configurado para executar a suíte.

O plugin **JaCoCo** está configurado no `pom.xml` com três goals:

- `prepare-agent` — instrumenta as classes durante a execução dos testes;
- `report` — gera o relatório HTML na fase `test`;
- `check` — roda na fase `verify` e **derruba o build** se a cobertura de linhas do bundle ficar abaixo de 90%.

## Como executar

```bash
./mvnw clean verify
```

O comando acima roda toda a suíte, gera o relatório e valida o limite de cobertura. Para gerar apenas o relatório:

```bash
./mvnw clean test
```

## Onde está o relatório

| Caminho | Descrição |
|---|---|
| `target/site/jacoco/index.html` | Relatório gerado pelo build (página inicial) |
| `target/site/jacoco/jacoco.csv` | Planilha com as métricas por classe |
| `target/site/jacoco/jacoco.xml` | Relatório em formato xml (CI) |
| `docs/relatorio-jacoco.pdf` | Print do `index.html` usado na entrega |

O diretório `target/` e a pasta `relatorio-jacoco/` são ignorados pelo Git — todo relatório é gerado sob demanda pelo build.

O `docs/relatorio-jacoco.pdf` é o print dessa página inicial. Como o plugin Maven do JaCoCo só sabe gerar HTML, XML e CSV, o PDF é obtido abrindo o `index.html` no navegador e usando *Imprimir → Salvar em PDF*.

## Cobertura atual

| Métrica | Cobertura |
|---|---|
| Linhas | 100% (541/541) |
| Instruções | 100% (2.049/2.049) |
| Branches | 100% (36/36) |
| Métodos | 100% (143/143) |

A suíte conta com **177 testes** automatizados, cobrindo as 24 classes que contêm regra de negócio: services, controllers, handler de exceções e o client de CEP.

### O que fica fora da medição

O `pom.xml` exclui da cobertura as classes que só carregam getters, setters, construtores e configuração:

| Excluído | Motivo |
|---|---|
| `entity/*` | Entidades JPA com Lombok — só accessors e anotações |
| `controller/dto/*` | DTOs de entrada e saída — só accessors |
| `config/*` | Classes de configuração do Spring |
| `PdvEdielApplication*` | Classe `main`, responsável por subir a aplicação |

O que permanece medido é o código com lógica: services, controllers, tratamento de exceções e o client de CEP.

======

# Planejamento do projeto

O projeto está sendo desenvolvido de forma incremental.

A estrutura atual foi criada pensando na expansão futura do sistema, mantendo as responsabilidades separadas entre Controller, Service, Repository e Entity.

As funcionalidades abaixo fazem parte do planejamento de evolução do projeto.

======

## Autenticação e usuários

Planejado:

- Implementação de login.
- Controle de acesso por perfil.
- Diferenciação das permissões entre DONO e FUNCIONARIO.
- Restrição de funcionalidades de acordo com o perfil.
- Implementação de segurança para as senhas.

======

## Estoque

Planejado:

- Registro automático das movimentações.
- Controle de entradas e saídas.
- Atualização automática da quantidade disponível.
- Histórico de movimentações.
- Validação de estoque disponível.

======

## Vendas

A estrutura inicial das entidades Venda e ItemVenda já está criada.

Planejado:

- Criação de vendas.
- Associação dos itens à venda.
- Cálculo automático do valor total.
- Registro da forma de pagamento.
- Registro do usuário responsável pela venda.
- Atualização do estoque após a venda.
- Finalização da venda.

Formas de pagamento previstas:

- DINHEIRO
- PIX
- CARTAO_CREDITO
- CARTAO_DEBITO

======


# Status do projeto

**Projeto em desenvolvimento.**

A primeira etapa do projeto tem como foco:

- Construção da API REST.
- Organização da arquitetura MVC.
- Implementação dos CRUDs principais.
- Integração com PostgreSQL.
- Utilização de DTOs.
- Validação dos dados.
- Tratamento de exceções.
- Estruturação das entidades e relacionamentos.

Novas funcionalidades serão adicionadas conforme o desenvolvimento do projeto.
