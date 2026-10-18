# CV Match

> Analise a aderência do seu currículo a uma vaga de emprego usando IA.

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen)
![Angular](https://img.shields.io/badge/Angular-18-red)
![License](https://img.shields.io/badge/license-MIT-blue)

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
| Frontend  | Angular 18 (standalone), Angular Material, Reactive Forms |
| Testes    | JUnit 5, Mockito, Testcontainers, Jasmine/Karma |
| DevOps    | Docker Compose, GitHub Actions |
| Docs      | Swagger / OpenAPI |

## Roadmap

- [x] Estrutura inicial do repositório
- [ ] Esqueleto do backend + PostgreSQL via Docker
- [ ] Entidades, migrations e repositories
- [ ] Autenticação com JWT e tratamento global de erros
- [ ] Upload de currículo (PDFBox) e cadastro de vagas
- [ ] Análise com Spring AI
- [ ] Histórico, comparação e dashboard
- [ ] Swagger, Dockerfile e CI
- [ ] Frontend Angular
- [ ] Docker Compose completo e documentação final

## Como rodar localmente

> Em construção — será completado ao final do projeto.

```bash
cp .env.example .env
docker compose up -d postgres
cd backend && ./mvnw spring-boot:run
```

## Licença

Distribuído sob a licença MIT. Veja [LICENSE](LICENSE).
