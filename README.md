# MediAgenda

Sistema de agendamento para a área da saúde: gerencia **profissionais de saúde**, **atendimentos** e **exames de laboratório**. Construído com Spring Boot, React, PostgreSQL, Docker e CI/CD.

## Tecnologias

- **Backend:** Java 17, Spring Boot 3.5, Spring Data JPA
- **Frontend:** React 19, Vite, TypeScript
- **Banco:** PostgreSQL 15
- **Infra:** Docker, GitHub Actions
- **Deploy:** Render (backend + banco) e Vercel (frontend)

## Estrutura

```
agenda-web/
├── backend/            # API REST (Spring Boot)
│   ├── src/main/...    # controller / service / repository / entity
│   └── Dockerfile
├── frontend/           # SPA (React + Vite + TS)
│   └── src/            # api / types / pages
├── docker-compose.yml  # PostgreSQL local
└── render.yaml         # Blueprint de deploy do backend + banco
```

## Rodar localmente

### 1. Banco de dados

```bash
docker compose up -d
```

> Se trocou o nome do banco e já tinha um volume antigo, recrie-o:
> `docker compose down -v && docker compose up -d`

### 2. Backend (porta 8080)

```bash
cd backend
./mvnw spring-boot:run
```

API em `http://localhost:8080` (ex.: `GET /profissionais`, `/atendimentos`, `/exames`).

### 3. Frontend (porta 5173)

```bash
cd frontend
npm install
npm run dev
```

App em `http://localhost:5173`. Em desenvolvimento, as chamadas `/api/*` são
redirecionadas para o backend pelo proxy do Vite (sem precisar de CORS).

## Variáveis de ambiente

### Backend

| Variável | Padrão | Descrição |
|---|---|---|
| `PORT` | `8080` | Porta do servidor |
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `agenda_web` | Nome do banco |
| `DB_USER` | `postgres` | Usuário |
| `DB_PASSWORD` | `postgres` | Senha |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origens liberadas no CORS (separadas por vírgula) |

### Frontend

| Variável | Descrição |
|---|---|
| `VITE_API_URL` | URL base do backend em produção. Em dev, deixe vazia para usar o proxy. |

## Testes

```bash
# Backend (JUnit + Mockito + testes de integração com H2)
cd backend && ./mvnw test

# Frontend (lint + type-check + build)
cd frontend && npm run build
```

## Deploy

CI/CD via GitHub Actions valida cada push; Render e Vercel fazem o deploy
automático a cada push na `main` (integração Git nativa).

### Backend + banco (Render)

1. Faça push do projeto para o GitHub.
2. No Render: **New → Blueprint** e conecte o repositório. O `render.yaml`
   cria o banco PostgreSQL e o web service do backend (via `backend/Dockerfile`).
3. Copie a URL gerada do backend (ex.: `https://mediagenda-backend.onrender.com`).

### Frontend (Vercel)

1. No Vercel: **New Project** e importe o repositório.
2. Defina **Root Directory = `frontend`**.
3. Adicione a variável `VITE_API_URL` com a URL do backend do Render.
4. Faça o deploy e copie a URL gerada (ex.: `https://mediagenda.vercel.app`).

### Liberar o CORS

No Render, no serviço do backend, defina `APP_CORS_ALLOWED_ORIGINS` com a URL do
Vercel e salve (faz um redeploy).

> **Free tier:** o backend no Render hiberna após inatividade (a primeira
> requisição pode levar ~30s) e o PostgreSQL gratuito expira em ~90 dias.

## Equipe

### Arthur Costa

Responsável pelo módulo de

### Pedro Felix

Responsável pelo módulo de
