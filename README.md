# Financial Validation & Test Automation Framework

### Java 21 • Playwright • JUnit 5 • SOAP • SQL Server

> Framework de automação orientado à validação de fluxos financeiros, integração entre camadas e regras de negócio.

Desenvolvido com foco em **qualidade de engenharia, rastreabilidade das validações, baixo acoplamento e evolução sustentável da automação**.

---

## Visão Geral

Este projeto nasceu com um objetivo que vai além da automação de interface:

**validar o comportamento financeiro de uma operação através de diferentes camadas do sistema.**

A estratégia combina automação Web, serviços, banco de dados e regras de domínio para permitir que uma mesma operação seja analisada desde sua preparação até o estado final persistido.

```text
Massa de teste
      ↓
Estado financeiro inicial
      ↓
Execução da operação
      ↓
Web / SOAP
      ↓
Processamento
      ↓
Estado financeiro final
      ↓
Validação das regras de negócio
```

A arquitetura foi estruturada para evitar que regras financeiras fiquem acopladas diretamente aos testes ou à tecnologia utilizada para executá-los.

---

# Arquitetura

O framework utiliza separação explícita de responsabilidades.

```text
Tests
  │
  ▼
Flows
  │
  ├───────────────┐
  ▼               ▼
Pages          Services
  │               │
  ▼               ▼
Playwright      SOAP / HTTP
                  │
                  ▼
             Repositories
                  │
                  ▼
              SQL Server

Domain
  ├── Models
  ├── Calculators
  ├── Validators
  └── Snapshots
```

Cada camada possui uma responsabilidade definida.

### Tests

Representam os cenários e resultados esperados.

O objetivo é manter os testes legíveis e livres de detalhes técnicos desnecessários.

### Flows

Orquestram jornadas que envolvem múltiplas ações.

Essa camada evita transformar os testes em sequências extensas de chamadas de Page Objects, serviços ou banco de dados.

### Pages

Encapsulam interação e comportamento da interface Web utilizando Playwright.

Os seletores são mantidos separados da lógica de navegação e das regras de negócio.

### Services

Responsáveis pela comunicação com serviços externos e operações SOAP/HTTP.

### Repositories

Centralizam o acesso aos dados persistidos.

Queries SQL não ficam espalhadas pelas classes de teste.

### Domain

Concentra regras e conceitos financeiros independentes da infraestrutura.

É nessa camada que ficam modelos, cálculos, validações e representações de estado financeiro.

---

# Estratégia de Validação Financeira

Uma das principais decisões do projeto é separar:

**execução da operação**

de

**validação do resultado financeiro.**

Um cenário pode seguir, por exemplo:

```text
1. Localizar uma massa elegível
          ↓
2. Consultar estado financeiro inicial
          ↓
3. Registrar um snapshot
          ↓
4. Executar a operação
          ↓
5. Aguardar/processar alteração
          ↓
6. Consultar estado financeiro posterior
          ↓
7. Aplicar regras de validação
```

Isso permite validar efeitos que não necessariamente aparecem imediatamente na interface.

---

# Financial Snapshot Strategy

Operações financeiras frequentemente alteram múltiplos registros e podem depender de processamentos posteriores.

Por esse motivo, o framework trabalha com o conceito de **snapshot financeiro**.

```text
BEFORE
────────────────────────

Conta
Saldo
Fatura
Pagamento
Parcelamento
Estado relevante


        OPERAÇÃO


AFTER
────────────────────────

Conta
Saldo
Fatura
Pagamento
Parcelamento
Novo estado
```

O validator compara os estados considerando a regra de negócio correspondente.

Essa abordagem reduz dependência de asserts isolados e facilita investigações de regressões.

---

# Cenários Financeiros

A estrutura suporta cenários como:

* pagamento integral;
* pagamento do valor mínimo;
* pagamento inferior ao mínimo;
* pagamento intermediário entre mínimo e saldo;
* pagamento superior ao saldo;
* quitação;
* geração e validação de boleto;
* alterações relacionadas a parcelamento;
* validações posteriores ao processamento.

Os valores dos cenários podem ser calculados dinamicamente a partir do contexto financeiro da conta.

Isso evita massas rígidas e valores arbitrários dentro dos testes.

---

# Validação em Múltiplas Camadas

Uma operação pode ser validada em diferentes pontos do sistema.

```text
               OPERAÇÃO
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
       WEB        SOAP        DB
        │          │          │
        └──────────┼──────────┘
                   ▼
             REGRA DE DOMÍNIO
                   │
                   ▼
              RESULTADO
```

O objetivo não é apenas confirmar que uma tela exibiu sucesso.

O framework busca validar que **o estado produzido pelo sistema corresponde ao comportamento financeiro esperado**.

---

# Web Automation

A camada Web utiliza **Playwright para Java**.

A estrutura separa:

```text
Test
 ↓
Flow
 ↓
Page
 ↓
Locators
 ↓
Playwright
```

Essa organização reduz duplicação e mantém mudanças de interface isoladas das regras do cenário.

---

# SOAP / HTTP

A camada de integração foi preparada para trabalhar com serviços SOAP através do Apache HttpClient.

Responsabilidades são separadas entre:

```text
Request
   ↓
Builder
   ↓
Client
   ↓
Response
   ↓
Parser
   ↓
Domain
```

Isso evita manipulação extensa de XML diretamente dentro dos testes.

---

# Database Validation

O SQL Server é tratado como uma camada própria da arquitetura.

```text
Test / Validator
       ↓
Repository
       ↓
Database Client
       ↓
SQL Server
```

O Repository Pattern mantém queries centralizadas e permite evolução das consultas sem contaminar os cenários de teste.

---

# Configuração por Ambiente

O framework possui configuração desacoplada da máquina onde é executado.

Exemplo:

```properties
environment=hml

base.url.hml=classpath:quitacao.html

browser=chrome
headless=false

viewport.width=1920
viewport.height=1080
```

Recursos locais podem ser resolvidos pelo classpath:

```text
classpath:quitacao.html
```

evitando caminhos absolutos como:

```text
C:\Users\usuario\projeto\...
```

Isso melhora a portabilidade entre máquinas e ambientes de execução.

---

# Segurança de Configuração

Credenciais e configurações locais não são versionadas.

O repositório utiliza arquivos de exemplo:

```text
application.properties.example
users.properties.example
```

Cada desenvolvedor cria sua configuração local a partir desses modelos.

Arquivos contendo informações locais são protegidos pelo `.gitignore`.

Essa estratégia evita exposição acidental de credenciais no histórico do Git.

---

# Fail Fast Configuration

Configurações obrigatórias são validadas antes da execução do cenário.

Em vez de permitir que uma propriedade inexistente provoque erros posteriores e pouco claros, o framework interrompe a execução na origem do problema.

Exemplo:

```text
Configuração obrigatória não encontrada: environment
```

Isso reduz o tempo de diagnóstico e torna falhas de infraestrutura mais previsíveis.

---

# Testabilidade da Infraestrutura

A configuração do framework também possui testes próprios.

Exemplo:

```text
EnvironmentConfigTest
```

Isso permite validar aspectos como:

```text
Ambiente
   ↓
Configuração
   ↓
Resolução da URL
   ↓
Execução
```

antes que um erro de configuração chegue à camada Web.

---

# Stack

| Área                  | Tecnologia          |
| --------------------- | ------------------- |
| Linguagem             | Java 21             |
| Web Automation        | Playwright          |
| Test Framework        | JUnit 5             |
| Assertions            | AssertJ             |
| Build                 | Maven               |
| Database              | SQL Server / JDBC   |
| HTTP / SOAP           | Apache HttpClient 5 |
| JSON                  | Jackson             |
| Reports               | Allure              |
| Excel                 | Apache POI          |
| Boilerplate Reduction | Lombok              |
| Test Data             | Datafaker           |
| Versionamento         | Git / GitHub        |

O `pom.xml` centraliza as versões das principais dependências para facilitar manutenção e atualização do framework.

---

# Estrutura Conceitual

```text
src
├── main
│   ├── java
│   │   └── com.automation
│   │       ├── config
│   │       ├── domain
│   │       ├── flow
│   │       ├── locators
│   │       ├── pages
│   │       ├── repository
│   │       ├── service
│   │       └── utils
│   │
│   └── resources
│       ├── requests
│       ├── application.properties.example
│       └── users.properties.example
│
└── test
    └── java
        └── com.automation
            └── tests
```

A estrutura evolui de acordo com as necessidades do domínio, preservando a separação entre infraestrutura, domínio e cenários.

---

# Decisões Arquiteturais

Decisões importantes do projeto são registradas através de **Architecture Decision Records (ADR)**.

Entre elas:

```text
ADR-001
Financial Domain-Oriented Platform

ADR-002
Financial Snapshot Strategy
```

O objetivo dos ADRs é registrar não apenas **o que foi implementado**, mas principalmente **por que determinada abordagem foi escolhida**.

Isso facilita manutenção, revisão técnica e evolução futura da plataforma.

---

# Qualidade do Código

Alguns princípios aplicados ao desenvolvimento:

* separação de responsabilidades;
* baixo acoplamento;
* alta coesão;
* composição sobre duplicação;
* regras de negócio independentes da infraestrutura;
* configuração externa ao código;
* reutilização controlada;
* legibilidade dos cenários;
* fail-fast para configurações inválidas;
* testes da própria infraestrutura de automação.

A prioridade é manter uma base que possa crescer sem transformar a suíte em um conjunto de scripts difíceis de manter.

---

# Execução

### Pré-requisitos

```text
Java 21
Maven 3.9+
Git
```

Clone o repositório:

```bash
git clone https://github.com/vanessalimaQA/automacao-quitacao.git
cd automacao-quitacao
```

Crie os arquivos locais de configuração a partir dos exemplos.

PowerShell:

```powershell
Copy-Item "src/main/resources/application.properties.example" "src/main/resources/application.properties"

Copy-Item "src/main/resources/users.properties.example" "src/main/resources/users.properties"
```

Execute:

```bash
mvn clean test
```

Para executar um teste específico:

```bash
mvn "-Dtest=NomeDoTeste" test
```

---

# Evidências e Relatórios

Os resultados do Allure são direcionados para:

```text
target/allure-results
```

A estrutura permite evolução da estratégia de evidências para cenários Web, integração e validações financeiras.

---

# Evolução da Plataforma

A arquitetura foi criada para permitir evolução incremental.

Entre os próximos passos estão:

```text
CI/CD
        ↓
GitHub Actions
        ↓
Execução automatizada
        ↓
Relatórios
        ↓
Evidências
        ↓
Regressão financeira
```

Também fazem parte da evolução planejada:

* ampliação da cobertura de APIs REST;
* execução parametrizada por ambiente;
* expansão da estratégia de massa;
* evolução dos relatórios;
* integração contínua;
* execução automatizada de regressões.

---

# O que este projeto demonstra

Mais do que conhecimento de uma ferramenta de automação, este projeto demonstra uma abordagem de Quality Engineering baseada em:

**entender o domínio → modelar o problema → separar responsabilidades → automatizar → observar o estado → validar o comportamento.**

A ferramenta é parte da solução.

A estratégia de qualidade é o que orienta a arquitetura.

---

# Sobre mim

Sou **Vanessa Santos**, profissional em desenvolvimento na área de Quality Engineering, com foco em automação de testes e construção de frameworks.

Tenho direcionado meus estudos e projetos para automação Web, integração de serviços, validações em banco de dados e arquitetura de testes utilizando Java.

Este projeto representa minha evolução prática na construção de uma solução de automação estruturada, com preocupação não apenas em fazer o teste executar, mas em criar uma base sustentável para sua evolução.

---

# Contato

**LinkedIn**
https://www.linkedin.com/in/vanessasantos-qa/

**GitHub**
https://github.com/vanessaSantosQA

---

> **Qualidade não termina quando o teste passa. Ela começa quando conseguimos confiar no comportamento que estamos validando.**

**Vanessa Santos — QA Automation | Quality Engineering**
