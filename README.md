# Portfolio Manager API

API REST para gerenciar o ciclo de vida de projetos, com Spring Boot, JPA/Hibernate, PostgreSQL, documentação OpenAPI e autenticação Basic.

## Executar

```bash
docker compose up --build
```

O banco PostgreSQL fica disponível na porta `5432` e a API em `http://localhost:8080`. A documentação interativa está em `http://localhost:8080/swagger-ui.html`.

Credenciais da API: `admin` / `admin123`.

Para executar localmente, inicie somente o banco (`docker compose up -d postgres`) e rode `./mvnw spring-boot:run`.

O Docker e o perfil `local` carregam automaticamente 10 membros e 10 projetos demonstrativos. Para desativar a carga, defina `APP_SEED_DATA=false`.

## Endpoints

- `POST, GET, PUT, DELETE /api/projects` — CRUD de projetos (a listagem aceita `name`, `status`, `startFrom`, `startTo`, `page` e `size`).
- `PATCH /api/projects/{id}/status` — transição controlada de status.
- `GET /api/portfolio/report` — indicadores resumidos do portfólio.
- `POST, GET /api/external/members` — mock da API REST externa de membros; membros não possuem cadastro interno.

Crie antes os membros externos e use seus IDs como `managerId` e `memberIds` em projetos. Somente a atribuição `funcionário` é elegível.

```json
POST /api/external/members
{"name":"Ana Silva","attribution":"funcionário"}

POST /api/projects
{
  "name":"Portal do Cliente",
  "startDate":"2026-06-01",
  "expectedEndDate":"2026-09-01",
  "totalBudget":100000,
  "description":"Novo portal de autoatendimento",
  "managerId":1,
  "memberIds":[1]
}
```

Os status seguem a sequência `EM_ANALISE → ANALISE_REALIZADA → ANALISE_APROVADA → INICIADO → PLANEJADO → EM_ANDAMENTO → ENCERRADO`; `CANCELADO` pode ser aplicado em qualquer etapa. A classificação de risco é calculada na resposta, sem persistência redundante.

## Testes

```bash
./mvnw test
```

O relatório de cobertura JaCoCo é gerado em `target/site/jacoco/index.html`.
