# fiap-14soat-tc-fase5-video-upload-service

![Java 21](https://img.shields.io/badge/Java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4.5-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_16-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-%23CC0200.svg?style=for-the-badge&logo=flyway&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI_3-%2385EA2D.svg?style=for-the-badge&logo=swagger&logoColor=black)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
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
- [🎬 Vídeos de Apresentação](#-vídeos-de-apresentação)
- [🔗 Repositórios Relacionados](#-repositórios-relacionados)

---

## 👤 Autor

| Nome                 | E-mail                  | RM        | Discord          | WhatsApp        |
|----------------------|-------------------------|-----------|------------------|-----------------|
| Jonas Fernando Schuh | jonasschuh@hotmail.com  | rm369458  | jonasf.schuh     | 47 9 9960-1396  |

**Grupo:** 2 · FIAP 14SOAT Fase 5 — Hackathon

---

## 📋 Descrição

Este repositório contém o **microserviço Video Upload** da plataforma **FIAP X** — responsável por receber vídeos dos usuários autenticados, armazená-los localmente (ou no S3 em produção) e publicar o evento `video.uploaded` no **RabbitMQ** (exchange `video.events`) para processamento assíncrono.

A aplicação é desenvolvida em **Spring Boot 3 (Java 21)** com arquitetura hexagonal (Ports & Adapters / Clean Architecture).

> ℹ️ **Infraestrutura compartilhada:** os recursos de infraestrutura (RabbitMQ, PostgreSQL) são provisionados pelo repositório [`fiap-14soat-tc-fase5-iac-terraform`](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-iac-terraform) via Kubernetes no Docker Desktop. Execute o `setup-cluster.sh` antes de iniciar este serviço.

### Principais funcionalidades

| Funcionalidade | Descrição |
|----------------|-----------|
| **Upload de Vídeo** | Recebe arquivo de vídeo via `multipart/form-data` e retorna `202 Accepted` imediatamente |
| **Armazenamento** | Salva o vídeo em pasta local (dev/K8s) ou S3 (AWS prod), de acordo com o profile ativo |
| **Publicação de Evento** | Publica mensagem `video.uploaded` no RabbitMQ (exchange `video.events`) para o `video-processing-service` consumir |
| **Listagem de Status** | Retorna vídeos; se `X-User-Id` for informado filtra por usuário, caso contrário retorna todos |
| **Detalhe do Vídeo** | Retorna o status e metadados de um vídeo específico |
| **Autenticação** | Proxy para o `auth` (login) — o `userId` é extraído do header `X-User-Id` injetado pelo API Gateway |
| **Teste RabbitMQ** | `POST /api/test/rabbitmq` — publica evento de teste para validar conectividade com o broker |

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
│   LocalFileStorageAdapter                                   │
│   RabbitVideoEventPublisherAdapter                          │
│   HttpCorrelationLoggingFilter                              │
│   Flyway Migrations                                         │
└────────────────────────────────────────────────────────────┘
```

### Fluxo de Upload

```
[Usuário]
    │  POST /api/videos (multipart/form-data)
    │  Header: Authorization: Bearer <JWT>
    ▼
[API Gateway] ──── [auth] ← valida JWT, injeta X-User-Id
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
             └── RabbitVideoEventPublisherAdapter → exchange: video.events / routing-key: video.uploaded
    │
    ▼
[202 Accepted] → { videoId, status: "PENDING" }
```

### Evento publicado no RabbitMQ (`video.events` / routing-key `video.uploaded`)

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

### Infraestrutura Local (Docker Compose + K8s)

```
┌─────────────────────────────────────────────────────────────────┐
│  fiap-network (bridge — compartilhada entre todos os serviços)   │
│                                                                   │
│  ┌─────────────────┐   ┌──────────────────┐                     │
│  │  video-upload   │   │   Adminer         │                     │
│  │  :8083          │   │   :8093           │                     │
│  │  (Spring Boot)  │   │   (UI PostgreSQL) │                     │
│  └────────┬────────┘   └──────────────────┘                     │
│           │                                                       │
│  Recursos provisionados pelo iac-terraform (Kubernetes):         │
│  ┌──────────────────┐   ┌──────────────────┐                     │
│  │  PostgreSQL       │   │    RabbitMQ       │                     │
│  │  localhost:5433   │   │  AMQP: 5672       │                     │
│  │  (video_upload_db)│   │  UI:   15672      │                     │
│  └──────────────────┘   └──────────────────┘                     │
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
| **Spring AMQP** | 3.x | Integração com RabbitMQ |
| **PostgreSQL** | 16 | Banco de dados relacional |
| **Flyway** | 10.x | Migrações de schema versionadas |
| **Swagger / OpenAPI** | 3.x | Documentação interativa da API |

### Mensageria

| Tecnologia | Ambiente | Uso |
|------------|----------|-----|
| **RabbitMQ** | Local/Docker/K8s | Exchange `video.events`, routing-key `video.uploaded` — provisionado pelo `iac-terraform` |

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
- [Docker Desktop 4.25+](https://www.docker.com/products/docker-desktop/) com Kubernetes habilitado
- **Infraestrutura provisionada** pelo [`fiap-14soat-tc-fase5-iac-terraform`](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-iac-terraform):

```bash
# No diretório do iac-terraform:
bash scripts/setup-cluster.sh
```

Isso provisiona no Kubernetes: PostgreSQL (`localhost:5433`), RabbitMQ AMQP (`localhost:5672`) e UI (`localhost:15672`).

---

### ⚙️ Rede Docker compartilhada

Crie a rede externa `fiap-network` uma única vez por máquina:

```bash
docker network create fiap-network
```

---

### Opção A — Apenas Adminer (aplicação na IDE) *(recomendado)*

```bash
docker-start-local-dev.bat
```

Ou diretamente:

```bash
docker compose up -d adminer
```

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **Adminer** | http://localhost:8093 | Interface web do PostgreSQL (`host.docker.internal:5433`) |
| **RabbitMQ UI** | http://localhost:15672 | Gerenciamento do broker (user: fiapx / pass: fiapx123) |

Execute a aplicação no IntelliJ com as variáveis do `.env.example`.

---

### Opção B — Container completo (API + Adminer)

```bash
docker compose up --build -d
```

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **API** | http://localhost:8083 | Video Upload Service |
| **Swagger UI** | http://localhost:8083/swagger-ui.html | Documentação interativa |
| **Adminer** | http://localhost:8093 | Interface web do PostgreSQL |

```bash
docker compose down
```

---

### Recursos criados pelo iac-terraform

| Recurso | Endereço local | Banco/VHost |
|---------|---------------|-------------|
| **PostgreSQL upload** | `localhost:5433` | `video_upload_db` |
| **RabbitMQ AMQP** | `localhost:5672` | vhost `fiapx` |
| **RabbitMQ UI** | http://localhost:15672 | user: `fiapx` / pass: `fiapx123` |

> ℹ️ O RabbitMQ é **compartilhado** entre todos os microserviços da stack via o mesmo broker K8s.

---

### Build da Aplicação (sem Docker)

```bash
mvn clean package -DskipTests
java -jar application/target/video-upload-application-*.jar \
  --spring.profiles.active=dev
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
| `POST` | `/auth/login` | ❌ | Proxy para auth (retorna JWT) |
| `POST` | `/api/videos` | ✅ | Upload de vídeo (`multipart/form-data`, campo `video`) |
| `GET` | `/api/videos` | ⚪ | Lista vídeos; filtra por usuário se `X-User-Id` for fornecido, retorna todos se omitido |
| `GET` | `/api/videos/{id}` | ✅ | Detalhe e status de um vídeo específico |
| `POST` | `/api/test/rabbitmq` | ❌ | Publica evento de teste no RabbitMQ (dev only) |

> ✅ = requer header `X-User-Id` · ⚪ = header opcional

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

### Exemplo — Listagem de Vídeos (com filtro de usuário)

```bash
curl http://localhost:8083/api/videos \
  -H "X-User-Id: user-123"
```

### Exemplo — Listagem de todos os vídeos (sem filtro)

```bash
curl http://localhost:8083/api/videos
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

### Exemplo — Teste de conectividade com RabbitMQ

```bash
curl -X POST "http://localhost:8083/api/test/rabbitmq?userId=user-123&filename=meu-video.mp4"
```

**Response 200 OK:**
```json
{
  "status": "published",
  "exchange": "video.events",
  "routingKey": "video.uploaded",
  "payload": {
    "videoId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user-123",
    "storageKey": "videos/user-123/550e8400-.../meu-video.mp4",
    "originalFilename": "meu-video.mp4",
    "fileSizeBytes": 1048576,
    "mimeType": "video/mp4",
    "timestamp": "2026-08-19T10:00:00Z",
    "testMessage": true
  }
}
```

> ⚠️ Este endpoint é destinado exclusivamente ao desenvolvimento local. Não habilitar em produção.

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
| Fase 5 | [Apresentação Tech Challenge 5 — RaceForce](https://youtu.be/PFs5ekuUxxM) |

---

## 🔗 Repositórios Relacionados

| Ordem | Repositório | Descrição |
|-------|-------------|-----------|
| 1 | [fiap-14soat-tc-fase5-iac-terraform](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-iac-terraform) | Banco de dados, RabbitMQ — infraestrutura K8s |
| 2 | [fiap-14soat-tc-fase5-auth](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-auth) | Login Authorizer            |
| 3 | [fiap-14soat-tc-fase5-video-upload-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-upload-service) | Upload + RabbitMQ publisher |
| 4 | [fiap-14soat-tc-fase5-video-processing-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-processing-service) | Processa vídeo, extrai frames, gera ZIP |
| 5 | [fiap-14soat-tc-fase5-video-status-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-status-service) | Status e metadados dos vídeos por usuário |
| 6 | [fiap-14soat-tc-fase5-video-download-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-video-download-service) | Download do ZIP via presigned URL |
| 7 | [fiap-14soat-tc-fase5-notification-service](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-notification-service) | Notificação por e-mail em caso de erro/conclusão |
| 8 | [fiap-14soat-tc-fase5-observability](https://github.com/jonasfschuh/fiap-14soat-tc-fase5-observability) | Prometheus + Grafana — dashboards e alertas |

---

## ⚙️ CI/CD — Configurando o Self-Hosted Runner

O pipeline de deploy deste repositório utiliza um **GitHub Actions self-hosted runner** rodando na máquina local com acesso ao cluster Kubernetes (Docker Desktop).

### Pré-requisitos do runner

Certifique-se de que a máquina possui instalado:

| Ferramenta | Versão mínima | Verificar |
|-----------|---------------|-----------|
| Docker Desktop (com K8s habilitado) | 4.x+ | `docker version` |
| kubectl | 1.28+ | `kubectl version --client` |
| Java 21 (JDK) | 21+ | `java -version` |
| Maven Wrapper | — | `.\mvnw.cmd -version` |

> Para o repositório IAC, também é necessário `terraform` (1.5+) e `helm` (3.x+).

### Passo a passo — configurar o runner

#### 1. Acesse as configurações do repositório no GitHub

```
GitHub → Repositório → Settings → Actions → Runners → New self-hosted runner
```

#### 2. Escolha o sistema operacional

Selecione **Windows** e a arquitetura **x64**.

#### 3. Baixe e configure o runner

Execute os comandos exibidos pelo GitHub na sua máquina local (PowerShell como Administrador):

```powershell
# Criar pasta para o runner (ajuste o caminho se necessário)
mkdir C:\actions-runner; cd C:\actions-runner

# Baixar o runner (substitua a URL pela exibida no GitHub)
Invoke-WebRequest -Uri https://github.com/actions/runner/releases/download/vX.X.X/actions-runner-win-x64-X.X.X.zip -OutFile actions-runner.zip

# Extrair
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory("$PWD\actions-runner.zip", "$PWD")

# Configurar (use o token gerado pelo GitHub na tela de configuração)
.\config.cmd --url https://github.com/<org>/<repo> --token <TOKEN-GERADO-PELO-GITHUB>
```

#### 4. Instalar como serviço Windows (recomendado)

```powershell
# Instalar e iniciar como serviço Windows (executa automaticamente no boot)
.\svc.cmd install
.\svc.cmd start

# Verificar status
.\svc.cmd status
```

#### 5. Verificar o runner no GitHub

```
GitHub → Repositório → Settings → Actions → Runners
```

O runner deve aparecer com status **Idle** (verde). A partir daí, qualquer push para `main` ou `develop` disparará o pipeline de deploy automaticamente.

### Verificar o deploy após o pipeline

```powershell
# Listar pods no namespace fiapx
kubectl get pods -n fiapx

# Verificar logs do serviço
kubectl logs -l app=<nome-do-app> -n fiapx --tail=50

# Acessar via Swagger (após NGINX Ingress estar ativo)
# http://localhost/<caminho>/swagger-ui.html
```

### Gerenciar o runner

```powershell
# Parar o serviço
.\svc.cmd stop

# Remover o serviço
.\svc.cmd uninstall

# Remover o runner do GitHub
.\config.cmd remove --token <TOKEN>
```

> 💡 **Dica:** Para múltiplos repositórios, crie uma pasta separada para cada runner (ex: `C:\actions-runner\auth`, `C:\actions-runner\upload`) e repita o processo para cada um.

<div align="center">

**🎓 Desenvolvido para o Tech Challenge FIAP 14SOAT — Fase 5 (Hackathon)**

*Projeto Acadêmico — Pós-Graduação em Arquitetura de Software · FIAP 2025/2026*

[⬆ Voltar ao topo](#fiap-14soat-tc-fase5-video-upload-service)

</div>

