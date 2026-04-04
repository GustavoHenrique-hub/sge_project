# SGE — Sistema de Gestão Escolar

Sistema web para gestão de instituições de ensino, desenvolvido com Java 17, JSF 4, PrimeFaces 13 e PostgreSQL. Cobre o ciclo completo da vida acadêmica: cadastro de alunos, turmas, profissionais e disciplinas, matrículas, lançamento de notas e frequência, geração de boletim e histórico.

🔗 **Deploy:** [https://sge-project-z6vr.onrender.com](https://sge-project-z6vr.onrender.com)  
🌿 **Branch ativa:** `dev`

---

## Funcionalidades

**Administração**
- Autenticação por sessão com timeout configurável por usuário
- Controle de acesso por perfil (Admin, Professor, Secretaria etc.)
- Cadastro de usuários, perfis e vínculo usuário–perfil
- Controle de situação (Ativo / Inativo)

**Gestão Escolar**
- Cadastro de alunos com validação de documentos (CPF/RG)
- Cadastro de turmas e disciplinas
- Cadastro de profissionais (professores e funcionários)
- Matrícula de alunos em turmas
- Vínculo de profissionais a disciplinas

**Módulo Acadêmico**
- Lançamento de notas por conceito: **I / R / B / MB**
- Lançamento de frequência por disciplina (em %)
- Geração automática de boletim consolidado por aluno
- Histórico acadêmico completo
- Cálculo de aprovação/reprovação por nota e frequência:
  - Aprovado por nota: conceito ≥ R
  - Aprovado por frequência: ≥ 75%
  - Reprovado por frequência: ≤ 25%
  - Faixa intermediária: DEPENDENTE_DA_NOTA

**Interface**
- Tema claro/escuro (ThemeBean)
- Layout responsivo com PrimeFaces + CSS customizado
- Componentes reutilizáveis (modais, inputs, botões)
- Dashboard com informações da sessão (nome, perfil, tempo restante)

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework Web | Jakarta Faces 4.0 (Mojarra) + PrimeFaces 13.0.10 |
| CDI | Weld 5.1.2 (weld-servlet-shaded) |
| ORM | Hibernate 6.4.4 / Jakarta Persistence 3 |
| Banco de dados | PostgreSQL |
| Driver JDBC | postgresql 42.7.3 |
| Build | Maven 3.9 |
| Servidor | Apache Tomcat 10.1 |
| Deploy | Docker + Render (plano Free) |
| Utilitários | Lombok, OpenPDF 1.3.39 |

---

## Pré-requisitos

- Java 17+
- Maven 3.9+
- Apache Tomcat 10.1+
- PostgreSQL 14+
- Docker (para deploy)

---

## Configuração local

### 1. Clone o repositório

```bash
git clone https://github.com/GustavoHenrique-hub/sge_project.git
cd sge_project
git checkout dev
```

### 2. Configure o banco de dados

```sql
CREATE DATABASE sge_db;
CREATE USER sge_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE sge_db TO sge_user;
```

### 3. Defina as variáveis de ambiente

O `JPAProducer` aceita **duas formas** de configuração — use a que preferir:

**Forma 1 — URL completa:**
```
DB_URL=jdbc:postgresql://localhost:5432/sge_db
DB_USER=sge_user
DB_PASSWORD=sua_senha
```

**Forma 2 — parâmetros separados:**
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sge_db
DB_USER=sge_user
DB_PASSWORD=sua_senha
```

No IntelliJ, configure em **Run → Edit Configurations → Tomcat → Startup/Connection → Environment variables**.

Variáveis opcionais para ajuste do Hibernate:

```
HIBERNATE_HBM2DDL_AUTO=update   # update | validate | none
HIBERNATE_SHOW_SQL=true
HIBERNATE_USE_SQL_COMMENTS=true
```

### 4. Build e deploy

```bash
mvn clean package -DskipTests
```

Copie o WAR gerado em `target/sge_project-1.0-SNAPSHOT.war` para `webapps/ROOT` do Tomcat, ou configure o IntelliJ para fazer o deploy automático.

### 5. Acesse a aplicação

```
http://localhost:8080/login.xhtml
```

---

## Estrutura do projeto

```
src/main/java/com/enterprise/
├── config/
│   ├── JPAProducer.java       # Produz EntityManager via CDI (@RequestScoped)
│   └── JpaTransaction.java    # Gerenciamento de transação RESOURCE_LOCAL
│
├── controller/
│   ├── academico/             # BoletimBean, FrequenciaBean, HistoricoBean,
│   │                          #   LancamentoBean, NotaBean
│   ├── admin/                 # AuthBean, UsuarioBean, PerfilBean,
│   │                          #   PerfilUsuarioBean, SituacaoBean, ThemeBean
│   ├── common/                # LayoutBean, DetalheModalBean
│   └── gestao/                # AlunoBean, TurmaBean, ProfissionalBean,
│                              #   DisciplinaBean, AlunoTurmaBean,
│                              #   ProfissionalDisciplinaBean
│
├── converter/                 # Conversores JSF para entidades JPA
├── dto/                       # DTOs por módulo (admin, gestao, academico)
├── filter/                    # AuthFilter — controle de acesso por sessão
│
├── model/
│   ├── entity/
│   │   ├── academico/         # BoletimEntity, FrequenciaEntity,
│   │   │                      #   HistoricoEntity, NotaEntity
│   │   ├── admin/             # UsuarioEntity, PerfilEntity,
│   │   │                      #   PerfilUsuarioEntity, SituacaoEntity
│   │   ├── embed/             # IDs compostos (AlunoID, TurmaID, ...)
│   │   └── gestao/            # AlunoEntity, TurmaEntity, ProfissionalEntity,
│   │                          #   DisciplinaEntity, AlunoTurmaEntity,
│   │                          #   ProfissionalDisciplinaEntity
│   └── enums/
│       └── academico/         # ConceitoNota (I, R, B, MB)
│
├── repository/                # Acesso ao banco via EntityManager
├── service/
│   ├── academico/             # BoletimService, FrequenciaService,
│   │                          #   HistoricoService, NotaService,
│   │                          #   AcademicoCalculoHelper
│   ├── admin/                 # PerfilService, UsuarioService, ...
│   └── gestao/                # AlunoService, TurmaService, ...
│
└── validation/                # DocumentsValidation (CPF, RG)

src/main/webapp/
├── pages/
│   ├── admin/                 # pageAdmin.xhtml, pageMatriculas.xhtml
│   ├── alunos/                # pageAlunos.xhtml, pageDashboard.xhtml,
│   │                          #   pageBoletim.xhtml, pageNotas.xhtml,
│   │                          #   pageFrequencia.xhtml, pageHistorico.xhtml,
│   │                          #   pageNotaFrequencia.xhtml, pageTurmas.xhtml
│   └── profissionais/         # pageProfissionais.xhtml, pageDisciplina.xhtml
├── components/modal/          # Modais e componentes de detalhe reutilizáveis
├── resources/
│   ├── components/            # Componentes base (input, button, modal...)
│   └── css/                   # app-v2.css (tema customizado)
├── login.xhtml
└── template.xhtml
```

---

## Autenticação e sessão

O sistema usa **sessão HTTP** gerenciada pelo `AuthBean` (`@SessionScoped`). Não há JWT nesta versão — a segurança é feita por sessão de servlet.

**Fluxo de login:**

1. Usuário informa CPF (login) e senha em `login.xhtml`
2. `PerfilUsuarioService.autenticar()` valida o vínculo ativo no banco
3. Em caso de sucesso, o `AuthBean` armazena o `PerfilUsuarioEntity` na sessão
4. O timeout da sessão é configurável por usuário (`session_timeout` em minutos)
5. O `AuthFilter` protege todas as rotas `*.xhtml`, redirecionando para login quando não autenticado
6. O dashboard exibe em tempo real o tempo restante de sessão

---

## Deploy no Render

### Build e run local com Docker

```bash
# Build
docker build -t sge-project .

# Run com URL completa
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host:5432/sge_db \
  -e DB_USER=sge_user \
  -e DB_PASSWORD=sua_senha \
  sge-project

# Run com parâmetros separados
docker run -p 8080:8080 \
  -e DB_HOST=localhost \
  -e DB_PORT=5432 \
  -e DB_NAME=sge_db \
  -e DB_USER=sge_user \
  -e DB_PASSWORD=sua_senha \
  sge-project
```

### Variáveis de ambiente no Render

| Variável | Descrição | Obrigatória |
|---|---|---|
| `DB_URL` | URL JDBC completa (substitui as abaixo) | Opcional |
| `DB_HOST` | Host do PostgreSQL | Sim (se sem `DB_URL`) |
| `DB_PORT` | Porta (padrão: `5432`) | Não |
| `DB_NAME` | Nome do banco | Sim (se sem `DB_URL`) |
| `DB_USER` | Usuário do banco | Sim |
| `DB_PASSWORD` | Senha do banco | Sim |
| `HIBERNATE_HBM2DDL_AUTO` | `update` / `validate` / `none` | Não |
| `HIBERNATE_SHOW_SQL` | `true` / `false` | Não |

---

## Modelo de dados

```
profissional ◄── usuario ──── acesso_usuario ──── perfil
                                   │
                                   └── situacao (ATIVO / INATIVO)

aluno ────────── aluno_turma ──────── turma

profissional ─── profissional_disciplina ─── disciplina

aluno_turma + disciplina ──► nota
                          ──► frequencia
                          ──► historico
                          ──► boletim
```

**Conceitos de nota (`ConceitoNota`):**

| Conceito | Descrição | Peso |
|---|---|---|
| I | Insatisfatório | 0 |
| R | Regular | 1 |
| B | Bom | 2 |
| MB | Muito Bom | 3 |

Aprovação por nota exige conceito ≥ **R** (peso ≥ 1).

---

## Licença

Projeto desenvolvido para fins acadêmicos.
