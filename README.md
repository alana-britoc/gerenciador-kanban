# Gerenciador Kanban

Projeto da disciplina de Padrões de Projeto. É um quadro Kanban simples, com as colunas **A fazer**, **Fazendo** e **Feito**, em que os cartões são criados, movidos entre as colunas e removidos.

O objetivo é aplicar três padrões do GoF, um de cada tipo:

| Tipo | Padrão | Onde entra |
| --- | --- | --- |
| Criacional | **Factory Method** | criação dos tipos de cartão (tarefa, bug, melhoria) |
| Estrutural | **Decorator** | extras de um cartão (prazo, prioridade, etiqueta) sem uma subclasse para cada combinação |
| Comportamental | **Observer** | quando um cartão muda de coluna, o histórico e as notificações são avisados |

Os detalhes e os diagramas de cada padrão estão em [`docs/padroes.md`](docs/padroes.md).

## Tecnologias

- **Back:** Java 21 com Spring Boot (API REST), Spring Data JPA e banco H2 salvo em arquivo
- **Front:** React com Vite

## Estrutura

```
gerenciador-kanban/
├── backend/                       # API em Spring Boot
│   └── src/main/java/br/com/kanban/
│       ├── config/                # cartões de exemplo do banco vazio
│       ├── controller/            # endpoints REST
│       ├── domain/                # Cartao (entidade), Coluna, TipoCartao
│       ├── repository/            # acesso ao banco (Spring Data JPA)
│       ├── service/               # regras do quadro
│       └── patterns/
│           ├── criacional/        # Factory Method
│           ├── estrutural/        # Decorator
│           └── comportamental/    # Observer
├── frontend/                      # tela do quadro em React
│   └── src/
│       ├── api/                   # chamadas para o back
│       └── components/
└── docs/
    └── padroes.md
```

## Como rodar

Precisa do **JDK 21** e do **Node.js 20** ou mais novo.

**Back** (sobe em http://localhost:8080):

```bash
cd backend
./mvnw spring-boot:run        # no Windows: mvnw.cmd spring-boot:run
```

**Front** (abre em http://localhost:5173):

```bash
cd frontend
npm install
npm run dev
```

O front encaminha as chamadas de `/api` para o back, então os dois precisam estar rodando.

Os dados ficam em `backend/data/` (banco H2 embutido, não precisa instalar nada) e continuam lá quando o back reinicia. Na primeira vez, com o banco vazio, o back cria dois cartões de exemplo. Para voltar ao quadro inicial, pare o back e apague a pasta `backend/data/`. Os testes usam um banco só em memória e não mexem nessa pasta.

**Testes do back:**

```bash
cd backend
./mvnw test
```

## API

| Método | Rota | O que faz |
| --- | --- | --- |
| GET | `/api/cartoes` | lista os cartões |
| POST | `/api/cartoes` | cria um cartão (`tipo`, `titulo`, `descricao` opcional, `detalhe` obrigatório) |
| PATCH | `/api/cartoes/{id}/coluna` | move o cartão (`coluna`: `A_FAZER`, `FAZENDO` ou `FEITO`) |
| POST | `/api/cartoes/{id}/extras` | adiciona um extra (`tipo`: `PRAZO`, `PRIORIDADE` ou `ETIQUETA`; `valor`: data `AAAA-MM-DD`, `BAIXA`/`MEDIA`/`ALTA` ou o texto da etiqueta) |
| DELETE | `/api/cartoes/{id}` | remove o cartão |
| PATCH | `/api/cartoes/{id}` | edita os textos do cartão (`titulo`, `descricao` opcional, `detalhe`) |
| DELETE | `/api/cartoes/{id}/extras/{tipo}` | remove um extra (`PRAZO`, `PRIORIDADE` ou `ETIQUETA`; para etiqueta, informe `?valor=`) |
