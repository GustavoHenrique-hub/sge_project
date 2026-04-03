# SGE — Sistema de Gestão Escolar

Sistema web para gestão de instituições de ensino, desenvolvido com Java 17, JSF, PrimeFaces e PostgreSQL. Permite o controle de alunos, turmas, profissionais, disciplinas, matrículas, boletins e lançamentos acadêmicos, com autenticação por sessão e controle de acesso por perfil.

🔗 **Deploy:** [https://sge-project-z6vr.onrender.com](https://sge-project-z6vr.onrender.com)

---

## Funcionalidades

- Autenticação com controle de sessão e timeout configurável por usuário
- Controle de acesso por perfil (Admin, Professor, Secretaria)
- Cadastro e gestão de alunos (CPF, RG, RM, data de nascimento, contato)
- Cadastro e gestão de profissionais (CPF, RG, RM, data de nascimento, contato)
- Cadastro e gestão de turmas e disciplinas
- Matrículas de alunos em turmas
- Vínculo de profissionais a disciplinas
- Boletim por aluno (vínculo aluno × disciplina × situação)
- Lançamento de notas e frequência
- Histórico acadêmico
- Tema claro/escuro
- Interface responsiva com PrimeFaces e Bootstrap

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework Web | Jakarta Faces 4 (JSF) + PrimeFaces 13 |
| CDI | Weld 5 |
| ORM | Hibernate 6 / Jakarta Persistence 3 |
| Segurança | Autenticação por sessão CDI + JWT (JJWT 0.11) |
| Banco de dados | PostgreSQL |
| Build | Maven 3.9 |
| Servidor | Apache Tomcat 10.1 |
| Deploy | Docker + Render |
| Utilitários | Lombok |

---

## Pré-requisitos

- Java 17+
- Maven 3.9+
- Apache Tomcat 10.1+
- PostgreSQL 14+
- Docker (para deploy em produção)

---

## Configuração local

### 1. Clone o repositório

```bash
git clone https://github.com/GustavoHenrique-hub/sge_project.git
cd sge_project
```

### 2. Configure o banco de dados

Crie um banco PostgreSQL local:

```sql
CREATE DATABASE sge_db;
CREATE USER sge_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE sge_db TO sge_user;
```

### 3. Configure as variáveis de ambiente

Defina as variáveis abaixo no seu sistema ou nas configurações do Tomcat no IntelliJ:

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sge_db
DB_USER=sge_user
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_com_minimo_32_caracteres
```

No IntelliJ, adicione em **Run/Debug Configurations → Tomcat → Startup/Connection → Environment variables**.

### 4. Build

```bash
mvn clean package -DskipTests
```

### 5. Acesse a aplicação

```
http://localhost:8080
```

---

## Credenciais padrão

Ao cadastrar um usuário, as credenciais são geradas automaticamente a partir do CPF do profissional vinculado:

| Campo | Valor padrão |
|---|---|
| Login | CPF (somente números) |
| Senha | `{CPF}_@ABC` |

Exemplo: CPF `123.456.789-00` → login `12345678900`, senha `12345678900_@ABC`.

---

## Estrutura do projeto

```
src/main/java/com/enterprise/
├── config/               # JPAProducer, JpaTransaction
├── controller/           # Beans JSF (ManagedBeans)
│   ├── academico/        # HistoricoBean, LancamentoBean
│   ├── admin/            # AuthBean, UsuarioBean, PerfilBean...
│   ├── common/           # LayoutBean, DetalheModalBean
│   └── gestao/           # AlunoBean, TurmaBean, ProfissionalBean...
├── converter/            # Conversores JSF para entidades
├── dto/
│   ├── academico/        # BoletimDTO
│   ├── admin/            # UsuarioDTO, PerfilDTO, SituacaoDTO...
│   └── gestao/           # AlunoDTO, TurmaDTO, ProfissionalDTO...
├── filter/               # AuthFilter (controle de sessão por requisição)
├── model/entity/
│   ├── academico/        # BoletimEntity (aluno_disciplina)
│   ├── admin/            # UsuarioEntity, PerfilEntity, PerfilUsuarioEntity, SituacaoEntity
│   ├── embed/            # IDs compostos (AlunoID, DisciplinaID, ProfissionalID, TurmaID)
│   └── gestao/           # AlunoEntity, TurmaEntity, ProfissionalEntity, DisciplinaEntity...
├── repository/           # Acesso ao banco via EntityManager
├── security/             # JwtUtil
├── service/              # Regras de negócio
└── validation/           # Validações customizadas (CPF, documentos)

src/main/webapp/
├── pages/                # Páginas XHTML por módulo
│   ├── admin/            # pageAdmin, pageMatriculas
│   ├── alunos/           # pageAlunos, pageDashboard, pageHistorico...
│   └── profissionais/    # pageProfissionais, pageDisciplina
├── components/modal/     # Modais de cadastro e detalhes
├── resources/components/ # Componentes base reutilizáveis
├── login.xhtml           # Página de login
└── template.xhtml        # Template base da aplicação
```

---

## Deploy no Render

O projeto está configurado para deploy automático via Docker no Render.

### Variáveis de ambiente necessárias

| Variável | Descrição |
|---|---|
| `DB_HOST` | Host do banco PostgreSQL |
| `DB_PORT` | Porta (padrão: `5432`) |
| `DB_NAME` | Nome do banco |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave secreta JWT (mínimo 32 caracteres) |

### Executar com Docker localmente

```bash
docker build -t sge_project .

docker run -p 8080:8080 \
  -e DB_HOST=localhost \
  -e DB_PORT=5432 \
  -e DB_NAME=sge_db \
  -e DB_USER=sge_user \
  -e DB_PASSWORD=sua_senha \
  -e JWT_SECRET=sua_chave_com_minimo_32_caracteres \
  sge_project
```

---

## Autenticação e segurança

O `AuthFilter` intercepta todas as requisições `.xhtml` e verifica se o usuário está autenticado via bean CDI de sessão (`AuthBean`). O fluxo é:

1. Usuário informa CPF (login) e senha
2. O sistema busca o vínculo ativo do usuário a um perfil na tabela `acesso_usuario`
3. A sessão é iniciada com os dados do usuário e perfil
4. O `AuthFilter` libera ou redireciona cada requisição conforme o estado da sessão
5. O timeout de sessão é configurável individualmente por usuário

### Perfis de acesso

| Perfil | Descrição |
|---|---|
| Admin | Acesso total ao sistema |
| Professor | Lançamentos, histórico e disciplinas |
| Secretaria | Matrículas, alunos e turmas |

---

## Modelo de dados

```
profissional ──────────────── usuario ──── acesso_usuario ──── perfil
                                                 │
                                                 └── situacao (ATIVO / INATIVO)

aluno ──────── aluno_turma ──────── turma

profissional ── profissional_disciplina ── disciplina

aluno + disciplina ──► aluno_disciplina (boletim) ──► situacao

aluno_turma + disciplina ──► lancamento (notas e frequência)
```

### Campos principais

**Aluno / Profissional**
- `id` — gerado aleatoriamente
- `rm` — registro de matrícula de 5 dígitos (gerado automaticamente)
- `cpf`, `rg`, `nome`, `dt_nasc`, `email`, `telefone`

**Usuário**
- Vinculado a um `Profissional` (chave composta: `profissional_id` + `profissional_rm`)
- `login` = CPF do profissional (sem pontuação)
- `senha` = CPF + `_@ABC` (padrão)
- `session_timeout` — timeout de sessão em minutos (configurável por usuário)

---

## Licença

Este projeto foi desenvolvido para fins acadêmicos.
