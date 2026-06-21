# Manager Product API

## Descrição do Projeto

A Manager Product API é uma aplicação backend desenvolvida em Spring Boot para gerenciamento de produtos, compradores,
fornecedores, pedidos e usuários. A API oferece operações CRUD completas para cada entidade, além de funcionalidades
específicas como gerenciamento de itens de pedido e atualização de status de pedidos.

## Tecnologias Utilizadas

- Java 17+
- Spring Boot 3+
- Spring Data JPA
- Lombok
- Hibernate Validator
- Spring Security
- JWT (JSON Web Tokens)
- Banco de dados relacional (configurável via properties)

## Estrutura do Projeto

O projeto segue uma arquitetura MVC (Model-View-Controller) com as seguintes camadas principais:

1. **Controllers**: Recebem as requisições HTTP e delegam a lógica para os serviços
2. **Services**: Contêm a lógica de negócios
3. **Repositories**: Interface com o banco de dados
4. **Models**: Entidades JPA que representam as tabelas do banco de dados
5. **DTOs**: Objetos de transferência de dados para comunicação com o cliente
6. **Security**: Configurações de autenticação e autorização

## Autenticação e Autorização

A API utiliza JWT (JSON Web Tokens) para autenticação. O fluxo de autenticação funciona da seguinte forma:

1. O cliente envia uma requisição POST para `/login` com credenciais válidas (email e senha)
2. O servidor valida as credenciais e retorna um token JWT no cabeçalho `Authorization`
3. O cliente deve incluir este token em todas as requisições subsequentes no cabeçalho `Authorization: Bearer <token>`
4. O servidor valida o token em cada requisição e concede acesso aos recursos conforme as permissões do usuário

### Configurações de Segurança

- Todas as rotas exigem autenticação por padrão
- Rotas públicas (não exigem autenticação):
    - `/api/users/register` - Registro de novos usuários
    - `/login` - Autenticação
    - `/swagger-ui/index.html` - Documentação da API
    - `/h2-console/**` - Console do banco de dados H2 (apenas para desenvolvimento)

### Exemplo de Requisição de Autenticação

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"senha123"}'
```

### Exemplo de Resposta

```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "Nome do Usuário",
  "cpf": "123.456.789-00",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Rotas da API

### 1. Compradores (Buyers)

- **POST** `/api/buyers` - Cria um novo comprador
- **GET** `/api/buyers/{id}` - Obtém um comprador pelo ID
- **GET** `/api/buyers` - Lista todos os compradores
- **PUT** `/api/buyers/{id}` - Atualiza um comprador existente
- **DELETE** `/api/buyers/{id}` - Remove um comprador

### 2. Pedidos (Orders)

- **POST** `/api/orders` - Cria um novo pedido
- **GET** `/api/orders/{id}` - Obtém um pedido pelo ID
- **GET** `/api/orders` - Lista todos os pedidos
- **GET** `/api/orders/{id}/items` - Lista os itens de um pedido específico
- **PUT** `/api/orders/{id}/status` - Atualiza o status de um pedido (parâmetro: `status`)
- **DELETE** `/api/orders/{id}` - Remove um pedido

### 3. Produtos (Products)

- **POST** `/api/products` - Cria um novo produto
- **GET** `/api/products/{id}` - Obtém um produto pelo ID
- **GET** `/api/products` - Lista todos os produtos
- **PUT** `/api/products/{id}` - Atualiza um produto existente
- **DELETE** `/api/products/{id}` - Remove um produto

### 4. Fornecedores (Suppliers)

- **POST** `/api/suppliers` - Cria um novo fornecedor
- **GET** `/api/suppliers/{id}` - Obtém um fornecedor pelo ID
- **GET** `/api/suppliers` - Lista todos os fornecedores
- **PUT** `/api/suppliers/{id}` - Atualiza um fornecedor existente
- **DELETE** `/api/suppliers/{id}` - Remove um fornecedor

### 5. Usuários (Users)

- **POST** `/api/users` - Cria um novo usuário
- **POST** `/api/users/register` - Registra um novo usuário (similar ao create, mas com tratamento específico)
- **GET** `/api/users/{id}` - Obtém um usuário pelo ID
- **GET** `/api/users/email/{email}` - Obtém um usuário pelo email
- **GET** `/api/users` - Lista todos os usuários (paginação: parâmetros `page` e `size`)
- **PUT** `/api/users/{id}` - Atualiza um usuário existente
- **DELETE** `/api/users/{id}` - Remove um usuário

## Modelo de Dados

As principais entidades do sistema são:

1. **Buyer**: Representa os compradores
2. **Supplier**: Representa os fornecedores
3. **Product**: Representa os produtos disponíveis
4. **Order**: Representa os pedidos feitos pelos compradores
5. **OrderItem**: Representa os itens individuais dentro de um pedido
6. **User**: Representa os usuários do sistema

Todas as entidades herdam de `BaseEntity` que inclui campos comuns como data de criação, atualização e usuário
responsável.

## Validações

A API utiliza validações como:

- Campos obrigatórios (`@NotBlank`)
- Formato de email válido (`@Email`)
- Validação de integridade de dados (ex: email único para usuários)

## Status de Pedidos

Os pedidos podem ter os seguintes status:

- `PENDING`: Aguardando processamento
- `PROCESSING`: Em processamento
- `COMPLETED`: Finalizado com sucesso
- `CANCELLED`: Cancelado

## Como Executar API

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/manager-product-api.git
   ```

2. Configure o arquivo `application.properties` com as credenciais do seu banco de dados

3. Execute a aplicação Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

4. A API estará disponível em `http://localhost:8080` (porta padrão)

## Como Executar a versão de Produção

1. Clone o repositório:
   ```bash
   git clone https://github.com/BioJJ/manager-product-application
   ```

2. Running the app

```bash
# development
$ docker-compose up -d

```

3. Acesse a aplicação pelo browser: localhost:4022

### LOGIN:

```bash
# Login: adm-manager@gmail.com
# Senha: Admin@2025
```

## Considerações

- Todas as rotas (exceto as públicas) exigem autenticação via JWT
- Em caso de erro, a API retorna mensagens descritivas com códigos HTTP apropriados

## Stay in touch

- Author - https://www.linkedin.com/in/jefferson-coelho/
- Website - https://github.com/BioJJ
- Twitter - https://twitter.com/bio_jefferson