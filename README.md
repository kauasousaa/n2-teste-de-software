# 📚 Livraria Páginas — Sistema de Vendas de Livros

Projeto N2 da disciplina **Testes de Software** (FATESG — 2026/02 — Prof. Reinaldo Jr).

Sistema full stack de vendas para o nicho de **livrarias**: controle do catálogo de livros, registro de vendas com baixa
automática de estoque e gestão dos usuários que acessam o sistema, com autenticação por login. O foco do projeto é a
**automação de testes** em três níveis: unitários (JUnit), de API (REST-assured) e de integração/E2E (Selenium).

## 👥 Integrantes

| Nome completo      |
|--------------------|
| Kauã Sousa Cardoso |
| Pedro Henrique Máximo |
| Júlia Inácio |
| Luiza Mel |

## 💡 Proposta

Uma livraria de bairro precisa abandonar as planilhas e ter um sistema simples para:

- **Cadastrar o catálogo de livros** com título, autor, ISBN, categoria, preço, estoque e data de publicação;
- **Registrar vendas** informando o livro, o cliente, a quantidade, a forma de pagamento e a data. O sistema congela o
  preço do livro no momento da venda, calcula o valor total e **baixa o estoque automaticamente**. Se a venda for alterada
  ou excluída, o estoque é **ajustado ou devolvido**;
- **Controlar quem acessa o sistema** (administradores e vendedores), com login, senha criptografada e possibilidade de
  inativar usuários.

### Regras de negócio implementadas

| Regra                                                                      | Onde está              |
|----------------------------------------------------------------------------|------------------------|
| Não é possível vender mais unidades do que o estoque disponível (HTTP 422) | `Livro.baixarEstoque`  |
| O valor total da venda é `preço do livro × quantidade`                     | `Venda.alterar`        |
| Alterar uma venda devolve o estoque antigo e baixa o novo                  | `VendaServiceImpl`     |
| Excluir uma venda devolve as unidades ao estoque                           | `VendaServiceImpl`     |
| Ao editar uma venda do mesmo livro, o preço original é mantido             | `Venda.alterar`        |
| ISBN único por livro e e-mail único por usuário (HTTP 409)                 | `*ServiceImpl`         |
| Não é possível excluir um livro que possui vendas (HTTP 409)               | `LivroServiceImpl`     |
| Preço deve ser maior que zero e estoque não pode ser negativo              | `Livro` + Bean Validation |
| Senhas armazenadas com hash BCrypt; usuário inativo não consegue logar     | `AuthServiceImpl`      |
| O usuário logado não pode excluir a si mesmo                               | `UsuarioServiceImpl`   |
| Rotas `/api/**` (exceto login) exigem token `Bearer` válido (HTTP 401)     | `AuthInterceptor`      |

## ✅ Requisitos atendidos

| Requisito do enunciado                              | Implementação                                                                 |
|-----------------------------------------------------|-------------------------------------------------------------------------------|
| 2 CRUDs de entidades de negócio (≥ 5 atributos)     | **Livro** (8 atributos) e **Venda** (8 atributos)                             |
| CRUD de Usuário com tela de login funcional         | **Usuário** (7 atributos) + `index.html` com autenticação por token           |
| Back-end com APIs                                   | API REST em Spring Boot (`/api/...`)                                          |
| Front-end consumindo as APIs                        | HTML + CSS + JavaScript (ES Modules) usando `fetch`                           |
| Banco de dados relacional                           | H2 (arquivo local, padrão) ou PostgreSQL (perfil `postgres`)                  |
| Testes unitários — JUnit (mín. 3)                   | **24 casos** em 5 classes                                                     |
| Testes de integração E2E — Selenium (mín. 2)        | **6 casos** em 3 classes (Page Object Pattern)                                |
| Testes de API — REST-assured (mín. 2)               | **12 casos** em 4 classes                                                     |

## 🛠️ Tecnologias utilizadas

- **Java 17** e **Spring Boot 3.5** (Web, Data JPA, Validation)
- **Hibernate / JPA** com **H2 Database** (padrão) e suporte a **PostgreSQL**
- **Spring Security Crypto** (BCrypt) para hash de senhas
- **HTML5, CSS3 e JavaScript** puro (ES Modules), servidos pelo próprio Spring Boot
- **JUnit 5** + **Mockito** — testes unitários
- **REST-assured 5** — testes de API
- **Selenium WebDriver 4** (Chrome) — testes E2E
- **Maven** (via Maven Wrapper, não é preciso instalar)

## 🏛️ Arquitetura

Arquitetura cliente-servidor em camadas, seguindo os princípios **SOLID** e **Clean Code**:

```
Navegador (HTML/CSS/JS)  ──HTTP/JSON──►  Controller  ──►  Service (interface)  ──►  Repository  ──►  Banco relacional
                                              │                  │
                                         DTOs (records)    Entidades de domínio ricas
                                                           (regras de estoque/preço)
```

- **S**ingle Responsibility: controllers só tratam HTTP, services orquestram casos de uso, entidades guardam as regras
  de domínio (ex.: `Livro.baixarEstoque`) e o `GlobalExceptionHandler` centraliza a tradução de erros para HTTP.
- **O**pen/Closed: novos catálogos (enums) são expostos sem alterar código graças à interface `EnumDescritivo`.
- **L**iskov / **I**nterface Segregation: interfaces pequenas e específicas por caso de uso (`LivroService`,
  `VendaService`, `AuthService`, `TokenService`...).
- **D**ependency Inversion: controllers e services dependem de abstrações (`TokenService`, `PasswordEncoder`, `Clock`,
  repositórios), injetadas por construtor, o que permite testar tudo com mocks.
- No front-end, a classe `PaginaCrud` reaproveita toda a lógica de listagem, cadastro, edição e exclusão; cada tela
  informa apenas as suas colunas e campos.

### Estrutura de pastas

```
src/main/java/br/com/fatesg/livraria
├── config/            # Beans, interceptor web e carga inicial de dados
├── controller/        # Endpoints REST + tratamento global de erros
├── domain/
│   ├── exception/     # Exceções de negócio
│   └── model/         # Entidades JPA e enums
├── dto/               # Records de request/response
├── repository/        # Spring Data JPA
├── security/          # Interceptor de autenticação e extração do token
└── service/           # Interfaces dos casos de uso
    └── impl/          # Implementações
src/main/resources/static   # Front-end (login, livros, vendas, usuários)
src/test/java/br/com/fatesg/livraria
├── unit/              # JUnit 5 + Mockito
├── api/               # REST-assured
├── e2e/               # Selenium (testes + pages/ com Page Objects)
└── support/           # Dados de teste reutilizáveis
```

### Modelo de dados

| Tabela     | Colunas                                                                                                   |
|------------|-----------------------------------------------------------------------------------------------------------|
| `livros`   | id, titulo, autor, isbn (único), categoria, preco, quantidade_estoque, data_publicacao                    |
| `vendas`   | id, livro_id (FK → livros), nome_cliente, quantidade, valor_unitario, valor_total, forma_pagamento, data_venda |
| `usuarios` | id, nome, email (único), senha_hash, perfil, ativo, data_cadastro                                          |

### Endpoints da API

Todos sob `/api`. Exceto o login, exigem o cabeçalho `Authorization: Bearer <token>`.

| Método | Rota                              | Descrição                                   |
|--------|-----------------------------------|---------------------------------------------|
| POST   | `/auth/login`                     | Autentica e devolve o token                 |
| POST   | `/auth/logout`                    | Encerra a sessão                            |
| GET    | `/auth/me`                        | Dados do usuário logado                     |
| GET    | `/livros?busca=`                  | Lista livros (busca por título ou autor)    |
| GET    | `/livros/{id}`                    | Detalha um livro                            |
| POST   | `/livros`                         | Cadastra um livro                           |
| PUT    | `/livros/{id}`                    | Atualiza um livro                           |
| DELETE | `/livros/{id}`                    | Exclui um livro                             |
| GET/POST/PUT/DELETE | `/vendas`, `/vendas/{id}`       | CRUD de vendas                    |
| GET/POST/PUT/DELETE | `/usuarios`, `/usuarios/{id}`   | CRUD de usuários                  |
| GET    | `/catalogos/categorias-livro`     | Categorias de livro                         |
| GET    | `/catalogos/formas-pagamento`     | Formas de pagamento                         |
| GET    | `/catalogos/perfis-usuario`       | Perfis de usuário                           |

Erros seguem o formato `{ "status", "mensagem", "detalhes", "timestamp" }`, com os códigos 400 (validação),
401 (não autenticado), 404 (não encontrado), 409 (conflito) e 422 (regra de negócio).

## ▶️ Como executar

### Pré-requisitos

- **JDK 17** ou superior (`java -version`)
- **Google Chrome** instalado (apenas para os testes Selenium; o driver é baixado automaticamente pelo Selenium Manager)
- Não é necessário instalar Maven: use o wrapper `mvnw` / `mvnw.cmd`

### Subir a aplicação

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Acesse **http://localhost:8080** e entre com o usuário criado automaticamente:

- **E-mail:** `admin@livraria.com`
- **Senha:** `admin123`

Na primeira execução também são cadastrados 3 livros de exemplo. Os dados ficam no arquivo `./data/livraria.mv.db`
(H2). O console do banco fica em http://localhost:8080/h2-console (JDBC URL `jdbc:h2:file:./data/livraria`, usuário `sa`,
sem senha).

### (Opcional) Usar PostgreSQL

```bash
docker compose up -d
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

As credenciais podem ser trocadas pelas variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`.

## 🧪 Testes automatizados

Os testes usam um banco H2 **em memória** (perfil `test`) e sobem a aplicação em uma porta aleatória, então não é
preciso que a aplicação esteja rodando.

| Nível                    | Framework           | Classes                                                                                              | Casos |
|--------------------------|---------------------|------------------------------------------------------------------------------------------------------|-------|
| Unitário                 | JUnit 5 + Mockito   | `VendaServiceImplTest`, `LivroServiceImplTest`, `AuthServiceImplTest`, `UsuarioServiceImplTest`, `InMemoryTokenServiceTest` | 24 |
| API                      | REST-assured        | `AuthApiTest`, `LivroApiTest`, `VendaApiTest`, `UsuarioApiTest`                                      | 12    |
| Integração (E2E)         | Selenium WebDriver  | `LoginE2ETest`, `LivroE2ETest`, `VendaE2ETest`                                                       | 6     |

### Comandos

```bash
# Todos os testes
mvnw.cmd test

# Somente um nível (tags JUnit: unit, api, e2e)
mvnw.cmd test -Dgroups=unit
mvnw.cmd test -Dgroups=api
mvnw.cmd test -Dgroups=e2e

# Selenium com o navegador visível (ideal para a gravação do vídeo)
mvnw.cmd test -Dgroups=e2e -Dselenium.headless=false
```

No Linux/macOS, troque `mvnw.cmd` por `./mvnw`. Os relatórios ficam em `target/surefire-reports`.

### O que cada nível cobre

- **Unitários:** cálculo do total e baixa de estoque na venda, venda com estoque insuficiente, ajuste de estoque ao
  editar/excluir venda, manutenção do preço histórico, ISBN duplicado, bloqueio de exclusão de livro com vendas,
  validação de preço (teste parametrizado), login válido/inválido/inativo, hash de senha, e-mail duplicado e expiração e
  revogação de token.
- **API:** login (200/401), acesso sem token (401), logout invalidando o token, CRUD completo de livros, validação
  (400), ISBN duplicado (409), datas históricas sem deslocamento de fuso, ciclo completo de venda com conferência do
  estoque, estoque insuficiente (422), exclusão de livro com vendas (409) e CRUD de usuário com login do usuário criado.
- **E2E (Selenium):** login com sucesso e logout, login com senha inválida, redirecionamento sem sessão, cadastro,
  edição e exclusão de livro pela interface, mensagens de validação e o fluxo completo de venda (cadastro do livro,
  total previsto, registro da venda, baixa e devolução do estoque).

## 🎥 Vídeo demonstrativo

> Link do vídeo: _adicionar aqui o link do vídeo (máx. 5 minutos)_

Roteiro sugerido:

1. Subir a aplicação (`mvnw.cmd spring-boot:run`) e fazer login como administrador.
2. Cadastrar, editar, buscar e excluir um livro.
3. Registrar uma venda, mostrar o total calculado e a baixa no estoque; editar e excluir a venda.
4. Cadastrar um usuário vendedor, sair e entrar com ele.
5. Executar `mvnw.cmd test -Dgroups=unit` e `mvnw.cmd test -Dgroups=api`.
6. Executar `mvnw.cmd test -Dgroups=e2e -Dselenium.headless=false` mostrando o Chrome sendo controlado.
