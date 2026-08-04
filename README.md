# fiap-14soat-tc-fase5-video-upload-service

![Java 21](https://img.shields.io/badge/Java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4.5-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-%23CC0200.svg?style=for-the-badge&logo=flyway&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI_3-%2385EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazonwebservices&logoColor=white)
![Amazon S3](https://img.shields.io/badge/Amazon_S3-%23569A31.svg?style=for-the-badge&logo=amazons3&logoColor=white)
![Amazon SQS](https://img.shields.io/badge/Amazon_SQS-%23FF9900.svg?style=for-the-badge&logo=amazonsqs&logoColor=white)
![Amazon EKS](https://img.shields.io/badge/Amazon_EKS-%23FF9900.svg?style=for-the-badge&logo=amazoneks&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/Amazon_RDS-%23527FFF.svg?style=for-the-badge&logo=amazonrds&logoColor=white)
![LocalStack](https://img.shields.io/badge/LocalStack-%23000000.svg?style=for-the-badge&logo=localstack&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-%23326CE5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
![New Relic](https://img.shields.io/badge/New_Relic-%231CE783.svg?style=for-the-badge&logo=newrelic&logoColor=white)
![Hexagonal Architecture](https://img.shields.io/badge/Hexagonal-Architecture-7B2D8B?style=for-the-badge)
![DDD](https://img.shields.io/badge/Domain--Driven_Design-430098?style=for-the-badge)
![Event-Driven](https://img.shields.io/badge/Event--Driven-FF6D00?style=for-the-badge)
![BDD](https://img.shields.io/badge/BDD-Cucumber-23D96C?style=for-the-badge&logo=cucumber&logoColor=white)
![Cucumber](https://img.shields.io/badge/Cucumber_7.18-%2323D96C.svg?style=for-the-badge&logo=cucumber&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-%2325A162.svg?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo_%E2%89%A580%25-green?style=for-the-badge)
![Mockito](https://img.shields.io/badge/Mockito_5-%23EE4C2C.svg?style=for-the-badge)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![Maven](https://img.shields.io/badge/Apache_Maven-%23C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)

---

## 📑 Sumário

- [👤 Autor](#-autor)
- [📋 Descrição](#-descrição)
- [🏗️ Arquitetura](#️-arquitetura)
- [🛠️ Tecnologias Utilizadas](#️-tecnologias-utilizadas)
- [🔒 Proteção da Branch main](#-proteção-da-branch-main)
- [🚀 Execução Local](#-execução-local)
- [🔌 API — Swagger e Endpoints](#-api--swagger-e-endpoints)
- [🧪 Testes](#-testes)
- [🔗 Repositórios Relacionados](#-repositórios-relacionados)

---

## 👤 Autor

| Nome                 | E-mail                  | RM        | Discord          | WhatsApp        |
|----------------------|-------------------------|-----------|------------------|-----------------|
| Jonas Fernando Schuh | jonasschuh@hotmail.com  | rm369458  | jonasf.schuh     | 47 9 9960-1396  |

**Grupo:** 2 · FIAP 14SOAT Fase 5 — Hackathon

---

## 📋 Descrição

Este repositório contém o **microserviço Video Upload** da plataforma **FIAP X** — responsável por receber vídeos dos usuários autenticados, armazená-los no **Amazon S3** (ou localmente em ambiente de desenvolvimento) e publicar o evento `video-uploaded` na fila **Amazon SQS** para processamento assíncrono.

A aplicação é desenvolvida em **Spring Boot 3 (Java 21)** com arquitetura hexagonal (Ports & Adapters / Clean Architecture).

> ⚠️ **Repositório master do ambiente local:** este repositório é o ponto central para execução local de toda a stack. O `docker-compose.yml` inclui o **LocalStack** (emulação de S3 + SQS + SNS) e o **StackPort** (browser de recursos AWS locais), compartilhados com todos os demais microserviços via `fiap-network`. As filas SQS usadas por todos os serviços são criadas pelo script `scripts/init-localstack.sh` presente neste repositório.

### Principais funcionalidades

| Funcionalidade | Descrição |
|----------------|-----------|
| **Upload de Vídeo** | Recebe arquivo de vídeo via `multipart/form-data` e retorna `202 Accepted` imediatamente |
| **Armazenamento** | Salva o vídeo no S3 (AWS) ou em pasta local (dev), de acordo com o profile ativo |
| **Publicação de Evento** | Publica mensagem `video-uploaded` no SQS para o `video-processing-service` consumir |
| **Listagem de Status** | Retorna a lista de vídeos enviados pelo usuário autenticado |
| **Detalhe do Vídeo** | Retorna o status e metadados de um vídeo específico |
| **Autenticação** | Proxy para o `auth-lambda` (login) — o `userId` é extraído do header `X-User-Id` injetado pelo API Gateway |

### Estrutura de Módulos Maven

```
fiap-14soat-tc-fase5-video-upload-service/
├── application/      → Controllers REST, DTOs, mappers, exception handlers, testes BDD (Cucumber)
├── domain/           → Modelos, use cases, ports de entrada e saída, exceções de domínio
├── infrastructure/   → Adapters JPA, S3, SQS, LocalStack, configurações, migrations Flyway
└── report-aggregate/ → Agregador de cobertura JaCoCo (multi-módulo)
```

---

## 🏗️ Arquitetura

### Arquitetura Hexagonal (Ports & Adapters)

```
┌────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│   VideoController  │  AuthProxyController  │  DTOs         │
│   GlobalExceptionHandler  │  SwaggerConfig                 │
└─────────────────────────┬──────────────────────────────────┘
                          │  Input Ports
┌─────────────────────────▼──────────────────────────────────┐
│                     Domain Layer                            │
│   Video (model)  │  VideoStatus (enum)                     │
│   UploadVideoUseCase  │  FindVideosByUserUseCase            │
│   FindVideoByIdUseCase                                      │
│   UploadVideoInputPort  │  FindVideosByUserInputPort        │
│   VideoRepositoryPort  │  VideoStoragePort                  │
│   VideoEventPublisherPort                                   │
└─────────────────────────┬──────────────────────────────────┘
                          │  Output Ports
┌─────────────────────────▼──────────────────────────────────┐
│                  Infrastructure Layer                       │
│   VideoRepositoryImpl (JPA)                                 │
│   LocalFileStorageAdapter  │  S3StorageAdapter              │
│   SqsVideoEventPublisherAdapter                             │
│   HttpCorrelationLoggingFilter  │  SqsMessageLogger         │
│   Flyway Migrations                                         │
└────────────────────────────────────────────────────────────┘
```

### Fluxo de Upload

```
[Usuário]
    │  POST /api/videos (multipart/form-data)
    │  Header: Authorization: Bearer <JWT>
    ▼
[API Gateway] ──── [auth-lambda] ← valida JWT, injeta X-User-Id
    │
    ▼
[VideoController]
    │  extrai userId do header X-User-Id
    ▼
[UploadVideoUseCase]
    │  valida tipo/tamanho do arquivo
    ├──► [VideoStoragePort]
    │        ├── LocalFileStorageAdapter  (profile: local/docker)
    │        └── S3StorageAdapter         (profile: aws/k8s/prod)
    │
    ├──► [VideoRepositoryPort] → salva com status PENDING
    │
    └──► [VideoEventPublisherPort]
             └── SqsVideoEventPublisherAdapter → SQS: video-uploaded
    │
    ▼
[202 Accepted] → { videoId, status: "PENDING" }
```

### Evento publicado no SQS (`video-uploaded`)

```json
{
  "videoId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-sub-cognito",
  "storageKey": "videos/user-id/uuid/video.mp4",
  "originalFilename": "video.mp4",
  "fileSizeBytes": 10485760,
  "mimeType": "video/mp4",
  "timestamp": "2025-01-01T10:00:00Z"
}
```

### Infraestrutura Local (Docker Compose)

```
┌─────────────────────────────────────────────────────────────────┐
│  fiap-network (bridge — compartilhada entre todos os serviços)   │
│                                                                   │
│  ┌─────────────────┐   ┌──────────────────┐   ┌──────────────┐ │
│  │  video-upload   │   │   LocalStack      │   │  StackPort   │ │
│  │  :8083          │   │   :4566           │   │  :8080       │ │
│  │  (Spring Boot)  │   │   S3 + SQS + SNS  │   │  (AWS UI)    │ │
│  └────────┬────────┘   └──────────────────┘   └──────────────┘ │
│           │                                                       │
│  ┌────────▼────────┐   ┌──────────────────┐                     │
│  │  PostgreSQL     │   │    Adminer        │                     │
│  │  :5433          │   │    :8093          │                     │
│  └─────────────────┘   └──────────────────┘                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tecnologias Utilizadas

### Core

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| **Java** | 21 | Linguagem da aplicação |
| **Spring Boot** | 3.4.5 | Framework principal |
| **Spring Data JPA** | 3.x | Persistência ORM |
| **PostgreSQL** | 16 | Banco de dados relacional |
| **Flyway** | 10.x | Migrações de schema versionadas |
| **AWS SDK v2** | 2.x | S3 + SQS |
| **Swagger / OpenAPI** | 3.x | Documentação interativa da API |

### Mensageria & Storage

| Tecnologia | Ambiente | Uso |
|------------|----------|-----|
| **Amazon SQS** | AWS | Fila `video-uploaded` (produção) |
| **Amazon S3** | AWS | Armazenamento de vídeos (produção) |
| **LocalStack** | Local/Docker | Emulação de SQS + S3 + SNS |
| **LocalStack StackPort** | Local/Docker | Browser visual de recursos AWS locais |

### Testes

| Ferramenta | Uso |
|------------|-----|
| **JUnit 5** | Testes unitários |
| **Mockito 5.x** | Mocks para testes unitários |
| **Cucumber 7.18** | Testes BDD (Behavior Driven Development) |
| **JaCoCo** | Cobertura de código (mínimo 80%) |

### DevOps & Infraestrutura

| Ferramenta | Versão | Uso |
|------------|--------|-----|
| **Docker** | 24.x | Containerização da aplicação |
| **Docker Compose** | 2.x | Orquestração local (master da stack) |
| **Kubernetes** | Latest | Orquestração em produção (EKS) |
| **Terraform** | Latest | IaC AWS (repositório iac-terraform) |
| **Maven** | 3.9+ | Build e gerenciamento de dependências |
| **New Relic** | 8.x | APM / Observabilidade |
| **GitHub Actions** | Latest | CI/CD |

---

## 🔒 Proteção da Branch main

As regras abaixo foram aplicadas em todos os repositórios da stack para atender ao requisito do Tech Challenge:

> *"Branch main protegida (sem commits diretos). Uso obrigatório de Pull Requests para merge. Deploy automático das branches de produção."*

### Regras configuradas no GitHub → Settings → Branches

| Regra | Valor |
|---|---|
| **Require a pull request before merging** | ✅ Ativado — bloqueia commits diretos na `main` |
| **Required approvals** | `1` revisão obrigatória antes do merge (OBS: desabilitado neste estudo — grupo com 1 pessoa) |
| **Dismiss stale reviews on new commits** | ✅ Ativado — revalida aprovação se o PR for atualizado |
| **Require status checks to pass** | ✅ Ativado — bloqueia merge se o PR Validation falhar |
| **Require branches to be up to date** | ✅ Ativado — evita merge de branch desatualizada |
| **Do not allow bypassing** | ✅ Ativado — nem o owner ignora as regras |

### Status check obrigatório neste repositório

| Check | Job no `pr-validation.yaml` |
|---|---|
| `build-and-test` | Build Maven + testes unitários + cobertura JaCoCo |

> ⚠️ O status check só aparece para seleção no GitHub após a **primeira execução bem-sucedida** do PR Validation.

---

## 🚀 Execução Local

### Pré-requisitos

- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker Desktop 4.25+](https://www.docker.com/products/docker-desktop/)

---

### ⚙️ Configuração da rede Docker compartilhada

Antes de subir qualquer serviço, crie a rede externa `fiap-network` (necessária uma única vez por máquina):

```bash
docker network create fiap-network
```

---

### Opção A — Stack completa com Docker Compose *(recomendado)*

Sobe a aplicação + PostgreSQL + LocalStack (S3/SQS/SNS) + StackPort + Adminer:

```bash
# Build e start de todos os serviços
docker compose up --build

# Apenas start (sem rebuild)
docker compose up

# Em background
docker compose up -d
```

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **API** | http://localhost:8083 | Video Upload Service |
| **Swagger UI** | http://localhost:8083/swagger-ui.html | Documentação interativa |
| **LocalStack** | http://localhost:4566 | Emulação de S3 + SQS + SNS |
| **StackPort** | http://localhost:8080 | Browser visual de recursos AWS locais |
| **Adminer** | http://localhost:8093 | Interface web do PostgreSQL |

```bash
# Parar os containers
docker compose down

# Parar e remover volumes (apaga dados do banco e LocalStack)
docker compose down -v
```

---

### Opção B — Apenas infraestrutura local (aplicação rodando na IDE)

```bash
# Subir apenas PostgreSQL, LocalStack e Adminer
docker compose up -d postgres-video-upload localstack adminer
```

Em seguida, execute a aplicação com o profile `local`:

```bash
./mvnw spring-boot:run -pl application \
  -Dspring-boot.run.arguments="--spring.profiles.active=local"
```

---

### Recursos criados automaticamente pelo LocalStack

O script `scripts/init-localstack.sh` é executado automaticamente na inicialização do container e cria:

| Tipo | Nome | Consumido por |
|------|------|--------------|
| **S3 Bucket** | `fiap-video-uploads` | `video-upload-service`, `video-processing-service` |
| **SQS Queue** | `video-uploaded` | `video-processing-service` |
| **SQS Queue** | `video-uploaded-dlq` | Monitoramento |
| **SQS Queue** | `video-events` | `video-status-service`, `notification-service` |
| **SQS Queue** | `video-events-dlq` | Monitoramento |

> ℹ️ Cada novo serviço adicionado à stack deve incluir suas filas neste script.

---

### Build da Aplicação (sem Docker)

```bash
# Compilar e empacotar
mvn clean package -DskipTests

# Executar (requer PostgreSQL e LocalStack rodando)
java -jar application/target/video-upload-application-*.jar \
  --spring.profiles.active=local
```

---

## 🔌 API — Swagger e Endpoints

### 📄 Swagger UI

| Ambiente | URL |
|----------|-----|
| **Local (Docker Compose)** | http://localhost:8083/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8083/v3/api-docs |

### Endpoints Disponíveis

| Método | Path | Auth | Descrição |
|--------|------|------|-----------|
| `POST` | `/auth/login` | ❌ | Proxy para auth-lambda (retorna JWT) |
| `POST` | `/api/videos` | ✅ | Upload de vídeo (`multipart/form-data`, campo `video`) |
| `GET` | `/api/videos` | ✅ | Lista vídeos do usuário autenticado |
| `GET` | `/api/videos/{id}` | ✅ | Detalhe e status de um vídeo específico |

> ✅ = requer header `X-User-Id` (injetado pelo API Gateway após validação JWT)

### Exemplo — Upload de Vídeo

```bash
curl -X POST http://localhost:8083/api/videos \
  -H "X-User-Id: user-123" \
  -F "video=@/path/to/video.mp4"
```

**Response 202 Accepted:**
```json
{
  "videoId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING",
  "originalFilename": "video.mp4",
  "createdAt": "2025-01-01T10:00:00"
}
```

### Exemplo — Listagem de Vídeos

```bash
curl http://localhost:8083/api/videos \
  -H "X-User-Id: user-123"
```

**Response 200 OK:**
```json
[
  {
    "videoId": "550e8400-e29b-41d4-a716-446655440000",
    "originalFilename": "video.mp4",
    "status": "PROCESSING",
    "fileSizeBytes": 10485760,
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-01T10:00:05"
  }
]
```

### Formatos de vídeo aceitos

`mp4`, `avi`, `mov`, `mkv`, `wmv`, `webm` — tamanho máximo: **500 MB** (configurável via `MAX_UPLOAD_SIZE`)

---

## 🧪 Testes

### Executar todos os testes

```bash
mvn clean test
```

### Executar apenas testes unitários (Domain)

```bash
mvn test -pl domain
```

### Executar apenas testes BDD (Cucumber - Application)

```bash
mvn test -pl application
```

### Executar com relatório de cobertura

```bash
mvn clean verify

# Abrir relatório (Windows)
start report-aggregate/target/site/jacoco-aggregate/index.html
```

### Estratégia de Testes

| Tipo | Ferramenta | Localização | Cobertura alvo |
|------|------------|-------------|----------------|
| Unitários (domain) | JUnit 5 + Mockito | `domain/` | ≥ 80% |
| BDD | Cucumber | `application/` | Fluxos principais |
| Unitários (infra) | JUnit 5 + Mockito | `infrastructure/` | ≥ 80% |

---

### 🎬 Vídeos de Apresentação

| Fase | Link |
|------|------|
| Fase 1 | [Apresentação Tech Challenge 1 — RaceForce](https://youtu.be/EKwE8l4yE1M) |
| Fase 2 | [Apresentação Tech Challenge 2 — RaceForce](https://youtu.be/95ml0-H9Vf4) |
| Fase 3 | [Apresentação Tech Challenge 3 — RaceForce](https://www.youtube.com/watch?v=KB-FC_4zsPE) |
| Fase 4 | [Apresentação Tech Challenge 4 — RaceForce](https://www.youtube.com/watch?v=vR3x4kW0l90) |
| Fase 5 | *(em desenvolvimento)* |

---

## 🔗 Repositórios Relacionados

| Ordem | Repositório | Descrição |
|-------|-------------|-----------|
| 1 | [fiap-14soat-tc-fase5-iac-terraform](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-iac-terraform) | VPC, ECS/EKS, S3, SQS, RDS, Cognito — infraestrutura AWS |
| 2 | [fiap-14soat-tc-fase5-auth-lambda](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-auth-lambda) | Lambda Authorizer + Cognito + API Gateway |
| 3 | [fiap-14soat-tc-fase5-video-upload-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-upload-service) | **Este repositório** — Upload + SQS publisher |
| 4 | [fiap-14soat-tc-fase5-video-processing-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-processing-service) | Processa vídeo, extrai frames, gera ZIP |
| 5 | [fiap-14soat-tc-fase5-video-status-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-status-service) | Status e metadados dos vídeos por usuário |
| 6 | [fiap-14soat-tc-fase5-video-download-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-download-service) | Download do ZIP via presigned URL S3 |
| 7 | [fiap-14soat-tc-fase5-notification-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-notification-service) | Notificação por e-mail em caso de erro/conclusão |
| 8 | [fiap-14soat-tc-fase5-observability](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-observability) | Prometheus + Grafana — dashboards e alertas |

---

<div align="center">

**🎓 Desenvolvido para o Tech Challenge FIAP 14SOAT — Fase 5 (Hackathon)**

*Projeto Acadêmico — Pós-Graduação em Arquitetura de Software · FIAP 2025/2026*

[⬆ Voltar ao topo](#fiap-14soat-tc-fase5-video-upload-service)

</div>

