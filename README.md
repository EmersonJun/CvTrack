# CV Match

> Analise a aderência do seu currículo a uma vaga de emprego usando IA.

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen)
![Angular](https://img.shields.io/badge/Angular-20-red)
![License](https://img.shields.io/badge/license-MIT-blue)
[![CI](https://github.com/EmersonJun/CvTrack/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/EmersonJun/CvTrack/actions/workflows/ci.yml)

## Sobre o projeto

O **CV Match** recebe um currículo em PDF e a descrição de uma vaga e devolve:

- Nota de aderência de **0 a 100**
- Palavras-chave **presentes** e **ausentes**
- **Pontos fortes** e **sugestões de melhoria**
- Histórico de análises com **comparação entre versões** do mesmo currículo
- **Dashboard** com a evolução da nota ao longo do tempo

## Stack

| Camada    | Tecnologias |
|-----------|-------------|
| Backend   | Java 21, Spring Boot 3, Spring Data JPA, Spring Security (JWT), Bean Validation, Spring AI, Apache PDFBox, MapStruct |
| Banco     | PostgreSQL 16, Flyway |
| Frontend  | Angular 20 (standalone, signals), Angular Material, Reactive Forms |
| Testes    | JUnit 5, Mockito, Testcontainers, Jasmine/Karma |
| DevOps    | Docker Compose, GitHub Actions |
| Docs      | Swagger / OpenAPI |

## Roadmap

- [x] Estrutura inicial do repositório
- [x] Esqueleto do backend + PostgreSQL via Docker
- [x] Entidades, migrations e repositories
- [x] Autenticação com JWT e tratamento global de erros
- [x] Upload de currículo com extração de texto (PDFBox)
- [x] Cadastro de vagas
- [x] Análise com Spring AI
- [x] Histórico, comparação e dashboard
- [x] Swagger, Dockerfile e CI
- [x] Frontend: estrutura, autenticação e layout
- [x] Frontend: currículos, vagas e análises
- [ ] Frontend: histórico, comparação e dashboard
- [ ] Docker Compose completo e documentação final

## Como rodar localmente

> Em construção — será completado ao final do projeto.

```bash
cp .env.example .env
docker compose up -d postgres
cd backend && ./mvnw spring-boot:run
```

Em outro terminal:

```bash
cd frontend
npm install
npm start
```

Acesse **http://localhost:4200**.

## Documentação da API

Com o backend rodando, acesse **http://localhost:8080/swagger-ui.html**.
Faça login em `POST /api/auth/login`, clique em **Authorize** e cole o token.

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
