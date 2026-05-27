# Viatio - API Back-end

Esta é a API REST que fornece suporte e persistência de dados para o aplicativo Viatio. Desenvolvida em Java com Spring Boot, a aplicação gerencia viagens, controle de despesas e transações de moedas estrangeiras, com suporte a autenticação JWT.

## Requisitos Prévios

Para executar a API em seu ambiente de desenvolvimento, são necessários:
- Java JDK 17 ou superior
- Apache Maven instalado (ou utilização do Maven Wrapper `./mvnw`)
- PostgreSQL instalado e em execução
- pgAdmin ou outro gerenciador de banco de dados SQL de sua preferência

## Configuração do Banco de Dados

1. Acesse o seu banco de dados PostgreSQL (utilizando o pgAdmin, por exemplo).
2. Crie uma base de dados vazia com o nome `viatio`.
3. Verifique se as credenciais configuradas no arquivo `src/main/resources/application.properties` correspondem às credenciais do seu ambiente. A configuração padrão é:
   - Porta: `5432`
   - Usuário: `postgres`
   - Senha: `1234`
4. As tabelas e chaves estrangeiras são criadas e atualizadas automaticamente pelo Hibernate/JPA (`spring.jpa.hibernate.ddl-auto=update`).

## Estrutura do Projeto

A organização dos arquivos segue a arquitetura de camadas padrão do ecossistema Spring Boot:

```
src/main/java/br/csi/viatio/
│
├── ViatioApplication.java          # Classe principal de inicialização da aplicação
│
├── controller/                     # Controladores REST que expõem os endpoints da API
│   ├── AuthController.java         # Autenticação de usuários (login e registro)
│   ├── CurrencyTransactionController.java # Transações de compra de moedas
│   ├── ExpenseController.java      # Lançamentos de despesas da viagem
│   ├── TripController.java         # Cadastro e listagem de viagens
│   └── WalletController.java       # Visualização e recálculo dos saldos
│
├── service/                        # Classes de lógica de negócios (Business Rules)
│   ├── CurrencyTransactionService.java
│   ├── ExpenseService.java
│   ├── TripService.java
│   └── WalletService.java
│
├── model/                          # Entidades JPA, Repositórios e DTOs (Data Transfer Objects)
│   ├── user/                       # Entidade User, repositórios e DTOs de login/registro
│   ├── trip/                       # Entidade Trip e DTOs
│   ├── expense/                    # Entidade Expense e DTOs
│   ├── currencytransaction/        # Entidade CurrencyTransaction e DTOs
│   └── wallet/                     # Entidade Wallet e chave composta WalletId
│
└── infra/                          # Infraestrutura, segurança e tratamento de erros
    ├── security/                   # Filtros de segurança JWT (Spring Security)
    ├── exception/                  # Tratamento global de exceções da API (Handler)
    └── DataInitializer.java        # Inicializador de dados de teste
```

## Como Executar a Aplicação

### Via IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Importe o projeto como um projeto Maven.
2. Aguarde o download das dependências.
3. Localize o arquivo `ViatioApplication.java` dentro de `src/main/java/br/csi/viatio/`.
4. Execute a classe principal (Run).

### Via Linha de Comando (Terminal)
No diretório raiz do projeto backend (`back-viatio`), execute:

```bash
# Limpar e compilar o projeto
./mvnw clean compile

# Rodar a aplicação utilizando o plugin do Spring Boot
./mvnw spring-boot:run
```

A aplicação será iniciada e estará acessível em `http://localhost:8081`.

## Principais Endpoints da API

A API é protegida por autenticação JWT (com exceção das rotas do `/auth`). Todas as requisições para rotas protegidas exigem o cabeçalho HTTP `Authorization: Bearer <seu_token>`.

### Autenticação (`/auth`)
- `POST /auth/register` - Cadastro de novo usuário.
- `POST /auth/login` - Login na aplicação (retorna o token JWT e dados do usuário).

### Viagens (`/trips`)
- `GET /trips` - Lista todas as viagens do usuário logado.
- `POST /trips` - Cadastro de nova viagem.
- `PUT /trips/{id}` - Edição de viagem existente.
- `DELETE /trips/{id}` - Exclusão lógica/física de viagem.

### Despesas (`/expenses`)
- `GET /expenses/trip/{tripId}` - Lista todos os gastos associados a uma viagem.
- `POST /expenses` - Registro de nova despesa.
- `PUT /expenses/{id}` - Edição de despesa.
- `DELETE /expenses/{id}` - Exclusão de despesa.

### Transações de Moeda (`/transactions`)
- `GET /transactions` - Histórico de transações de moedas.
- `POST /transactions` - Adiciona uma transação de câmbio.
- `PUT /transactions/{id}` - Edita uma transação de câmbio.
- `DELETE /transactions/{id}` - Exclui uma transação.

### Carteira/Saldos (`/wallets`)
- `GET /wallets` - Consulta a carteira física consolidada do usuário logado.