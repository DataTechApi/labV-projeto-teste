# 📦 Trabalho Semestral — Verificação e Validação de Software

> **FATEC** | Laboratório de Verificação e Validação (LABV) | Novembro / 2026

---

## 📋 Sobre o Trabalho

Este repositório demonstra, de forma prática e comparativa, como **técnicas estruturadas de projeto de casos de teste** reduzem o esforço de teste sem perder cobertura — aplicadas a um **sistema de vendas em Java**.

Dois projetos implementam o **mesmo domínio de negócio** com abordagens opostas:

| Projeto | Abordagem | Casos | Cobertura |
|---|---|---|---|
| [`vendas-testing`](#1-vendas-testing--com-técnicas) | Pairwise + Complexidade Ciclomática | **33 casos** | **100%** |
| [`vendas-testes-tradicionais`](#2-vendas-testes-tradicionais--sem-técnicas) | Ad-hoc / Tradicional | **48 casos** | **~87,5%** |

---

## 🗂️ Estrutura do Repositório

```
trabalho semestral/
│
├── 📁 vendas-testing/                   ← Projeto 1: COM técnicas estruturadas
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/fatec/vendas/
│       │   ├── model/                   ← Entidades e Enums
│       │   ├── service/                 ← Lógica de negócio (CC documentada)
│       │   └── exception/
│       └── test/java/com/fatec/vendas/testing/
│           ├── pairwise/                ← 9 casos pairwise + matriz
│           ├── ciclomatica/             ← Testes por CC (Desconto, Frete, Venda)
│           └── integracao/              ← 4 cenários end-to-end
│
├── 📁 vendas-testes-tradicionais/       ← Projeto 2: SEM técnicas (comparativo)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/fatec/vendas/  ← Mesmo domínio do Projeto 1
│       └── test/java/com/fatec/vendas/tradicional/
│           ├── TesteTradicionalTipoClienteTest.java
│           ├── TesteTradicionalPagamentoTest.java
│           ├── TesteTradicionalRegiaoTest.java
│           ├── TesteTradicionalValorTest.java
│           ├── TesteTradicionalDescontoTest.java
│           ├── TesteTradicionalFreteTest.java
│           └── TesteTradicionalCombinacoesTest.java
│
├── 📄 Pairwise_CC_Vendas_FATEC.pptx    ← Apresentação (17 slides, inclui comparativo)
├── 📄 Pairwise_CC_Vendas_FATEC.pdf     ← Documento técnico completo
└── 📄 Comparativo_Tradicional_vs_Tecnicas.pdf  ← Relatório comparativo
```

---

## ⚙️ Tecnologias

| Ferramenta | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem principal |
| Maven | 3.x | Build e gestão de dependências |
| JUnit 5 | 5.10.2 | Framework de testes |
| Mockito | 5.11.0 | Dublês de teste (mocks) |
| AssertJ | 3.25.3 | Asserções fluentes |
| JaCoCo | 0.8.11 | Relatório de cobertura de código |

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+ instalado
- Maven 3.6+ instalado

### Projeto 1 — Com Técnicas

```bash
cd vendas-testing

# Executar todos os testes
mvn test

# Executar apenas testes Pairwise
mvn test -Dgroups=pairwise

# Executar apenas testes de CC
mvn test -Dgroups=complexidade-ciclomatica

# Executar apenas testes de integração
mvn test -Dgroups=integracao

# Gerar relatório de cobertura JaCoCo
mvn jacoco:report
# Abrir: target/site/jacoco/index.html
```

### Projeto 2 — Testes Tradicionais

```bash
cd vendas-testes-tradicionais

# Executar todos os testes
mvn test

# Gerar relatório de cobertura JaCoCo
mvn jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## 1. `vendas-testing` — Com Técnicas

### 🎯 Pairwise Testing

**O que é:** técnica que garante que cada par de valores dos parâmetros de entrada seja testado pelo menos uma vez. Baseada na observação de que a maioria dos defeitos vem da interação de **2 fatores**, não de todos.

**Parâmetros do sistema de vendas:**

| # | Parâmetro | Valores |
|---|---|---|
| P1 | Tipo de Cliente | `COMUM`, `VIP`, `REVENDEDOR` |
| P2 | Forma de Pagamento | `PIX`, `CARTAO_CREDITO`, `BOLETO` |
| P3 | Região de Entrega | `SUDESTE`, `NORDESTE`, `NORTE` |
| P4 | Faixa de Valor | `< R$100`, `R$100–500`, `> R$500` |

**Resultado:**

| Métrica | Valor |
|---|---|
| Combinações brutas (3⁴) | **81** |
| Casos com Pairwise | **9** |
| Economia | **89%** |
| Pares cobertos | **54 / 54 (100%)** |

**Matriz de 9 casos:**

| TC | Cliente | Pagamento | Região | Valor | Desc. | Frete |
|---|---|---|---|---|---|---|
| 01 | COMUM | PIX | SUDESTE | R$50 | 5% | R$15 |
| 02 | COMUM | CARTÃO | NORDESTE | R$250 | 0% | R$20 |
| 03 | COMUM | BOLETO | NORTE | R$750 | 6% | R$45 |
| 04 | VIP | PIX | NORDESTE | R$750 | 23% | R$20 |
| 05 | VIP | CARTÃO | NORTE | R$50 | 10% | R$45 |
| 06 | VIP | BOLETO | SUDESTE | R$250 | 8% | R$15 |
| 07 | REVENDEDOR | PIX | NORTE | R$250 | 20% | R$45 |
| 08 | REVENDEDOR | CARTÃO | SUDESTE | R$750 | 23% | R$0 (grátis) |
| 09 | REVENDEDOR | BOLETO | NORDESTE | R$50 | 13% | R$35 |

---

### 🧮 Complexidade Ciclomática (CC)

**O que é:** métrica de McCabe (1976) que conta os **caminhos independentes** no grafo de fluxo de controle de um método, determinando o número **mínimo de testes** para cobertura completa de ramos.

**Fórmula:** `CC = E − N + 2×P`

> E = arestas, N = nós, P = componentes conexos

**Análise dos Services:**

| Método | Nós (N) | Arestas (E) | **CC** | Testes mínimos |
|---|---|---|---|---|
| `DescontoService.calcularDesconto()` | 14 | 20 | **8** | 8 casos |
| `FreteService.calcularFrete()` | 12 | 15 | **5** | 5 casos |
| `VendaService.processarVenda()` | 15 | 18 | **5** | 5 casos |

**Regras de desconto (ramos mapeados):**

```
[VIP]        → +10%
[REVENDEDOR] → +15%
[PIX]        → +5%
[BOLETO]     → −2% (taxa)
[valor>500]  → +8%
[total>25%]  → teto de 25%
[total<0%]   → proteção: mínimo 0%
```

**Regras de frete (CC=5, 5 caminhos):**

```
SUDESTE + valor > R$300  → R$0,00 (grátis)
SUDESTE + valor ≤ R$300  → R$15,00
NORDESTE + valor > R$150 → R$20,00
NORDESTE + valor ≤ R$150 → R$35,00
NORTE                    → R$45,00
```

---

### 🧪 Suíte de Testes — Projeto 1

| Arquivo | Técnica | Casos | Tags |
|---|---|---|---|
| `PairwiseVendaTest` | Pairwise | 9 | `pairwise` |
| `DescontoServiceTest` | Complexidade Ciclomática | 8 | `complexidade-ciclomatica` |
| `FreteServiceTest` | CC + Fronteira + Erro | 7 | `complexidade-ciclomatica` |
| `VendaServiceTest` | CC + Mockito | 5 | `complexidade-ciclomatica` |
| `VendaFluxoCompletoTest` | Integração | 4 | `integracao` |
| **Total** | | **33** | |

**Resultado:** ✅ 33/33 — BUILD SUCCESS | Cobertura JaCoCo > 90%

---

### 📁 Estrutura de Código — Projeto 1

```
src/main/java/com/fatec/vendas/
├── model/
│   ├── TipoCliente.java       (enum: COMUM, VIP, REVENDEDOR)
│   ├── FormaPagamento.java    (enum: PIX, CARTAO_CREDITO, BOLETO)
│   ├── RegiaoEntrega.java     (enum: SUDESTE, NORDESTE, NORTE)
│   ├── Cliente.java
│   ├── Produto.java
│   ├── ItemVenda.java
│   └── Venda.java
├── service/
│   ├── DescontoService.java   ← CC=8 | grafo de fluxo documentado
│   ├── FreteService.java      ← CC=5 | grafo de fluxo documentado
│   └── VendaService.java      ← CC=5 | orquestrador com validações
└── exception/
    └── VendaException.java

src/test/java/com/fatec/vendas/testing/
├── pairwise/
│   ├── PairwiseMatrix.java    ← matriz 9×4 e cálculo de pares cobertos
│   └── PairwiseVendaTest.java ← TC01 a TC09 com @DisplayName e pares anotados
├── ciclomatica/
│   ├── DescontoServiceTest.java ← P1 a P8 mapeados explicitamente
│   ├── FreteServiceTest.java    ← P1 a P5 + fronteiras + erro
│   └── VendaServiceTest.java   ← P1 a P5 com @Mock e verify()
└── integracao/
    └── VendaFluxoCompletoTest.java ← 4 cenários end-to-end sem mocks
```

---

## 2. `vendas-testes-tradicionais` — Sem Técnicas

### ❌ O que é a abordagem tradicional (ad-hoc)?

Forma mais comum de escrever testes **sem treinamento específico**:
- Testa cada parâmetro **isoladamente** (sem cruzar com outros)
- **Repete combinações** sem perceber (redundância)
- **Não sabe quando parar** — sem critério matemático
- **Não mapeia caminhos** — sem identificar os if/else não testados
- Resultado: mais trabalho, menos cobertura

### 🧪 Suíte de Testes — Projeto 2

| Arquivo | Foco | Casos | Problema |
|---|---|---|---|
| `TesteTradicionalTipoClienteTest` | Tipo de cliente (isolado) | 5 | Sempre fixa PIX+SUDESTE |
| `TesteTradicionalPagamentoTest` | Forma de pagamento | 6 | Sempre fixa SUDESTE |
| `TesteTradicionalRegiaoTest` | Região (isolada) | 5 | Nunca testa frete grátis |
| `TesteTradicionalValorTest` | Faixa de valor | 6 | 2 casos redundantes |
| `TesteTradicionalDescontoTest` | Desconto | 12 | 3 redundantes, P1 faltando |
| `TesteTradicionalFreteTest` | Frete | 6 | 4 redundantes |
| `TesteTradicionalCombinacoesTest` | Ad-hoc final | 8 | 2 redundantes |
| **Total** | | **48** | |

**Resultado:** ✅ 48/48 — BUILD SUCCESS (todos passam, mas cobertura é incompleta)

---

## ⚖️ Comparativo Completo

### Visão geral

| Métrica | 🔴 Tradicional | ✅ Com Técnicas | Diferença |
|---|---|---|---|
| Total de casos | **48** | **33** | −31% |
| Casos redundantes | **~15 (27%)** | **0 (0%)** | −100% |
| Cobertura de pares | **53/54 (98%)** | **54/54 (100%)** | +1 par |
| Caminhos CC Desconto | **7/8 (87,5%)** | **8/8 (100%)** | +12,5% |
| Caminhos CC Frete | **5/5 (100%)** | **5/5 (100%)** | Igual |
| Rastreabilidade | **Nenhuma** | **Total** | ∞ |
| Bug P1 detectado? | **❌ NÃO** | **✅ SIM** | Crítico |
| Tempo de escrita (est.) | **~4 horas** | **~1,5 horas** | −62% |
| Manutenibilidade | **Baixa** | **Alta** | — |

### Redundâncias no projeto tradicional (~27%)

| Combinação duplicada | Aparece em | Nº de vezes |
|---|---|---|
| COMUM + PIX + SUDESTE + R$50 | TipoCliente, Pagamento, Valor, Desconto | **4×** |
| COMUM + CARTÃO + SUDESTE + R$200 | Pagamento, Região, Frete, Desconto | **4×** |
| NORDESTE + CARTÃO + COMUM + R$200 | TipoCliente, Região, Frete | **3×** |
| VIP + PIX + SUDESTE + R$200 | TipoCliente, Pagamento | **2×** |
| VIP + PIX + NORDESTE + R$600 | Valor, Combinações | **2×** |

### O caminho crítico não coberto (P1 — DescontoService)

> **Com mais casos de teste (48 vs 33), o projeto tradicional cobre MENOS caminhos.**

O caminho **P1** do `DescontoService` — `COMUM + BOLETO + valor baixo` — resulta em desconto de **−2%**, que deve ser zerado pela proteção do sistema. Este caminho **nunca foi testado** no projeto tradicional porque:

- Sempre que o testador usou `BOLETO`, misturou com `VIP` (+10%) ou `REVENDEDOR` (+15%)
- Esses bônus mascaram a taxa de −2%, nunca exercitando o zero-protection
- **Sem a análise de CC**, não há como saber que faltou exatamente esse caminho

```
// Código com potencial bug (sem o guard):
if (desconto.compareTo(BigDecimal.ZERO) < 0) {
    desconto = BigDecimal.ZERO;  // ← esta linha nunca foi exercitada
}
```

---

## 📊 Arquivos de Apresentação

| Arquivo | Conteúdo |
|---|---|
| [`Pairwise_CC_Vendas_FATEC.pptx`](./Pairwise_CC_Vendas_FATEC.pptx) | 17 slides: conceitos, matrizes, grafos CC, comparativo |
| [`Pairwise_CC_Vendas_FATEC.pdf`](./Pairwise_CC_Vendas_FATEC.pdf) | Documento técnico completo (Pairwise + CC) |
| [`Comparativo_Tradicional_vs_Tecnicas.pdf`](./Comparativo_Tradicional_vs_Tecnicas.pdf) | Relatório de comparação entre os dois projetos |

---

## 📚 Conceitos Chave Demonstrados no Código

### No projeto `vendas-testing`

| Conceito | Onde ver |
|---|---|
| **Pairwise matrix** | `PairwiseMatrix.java` — tabela 9×4 com pares cobertos |
| **CC documentada** | `DescontoService.java` — grafo de fluxo em Javadoc |
| **Padrão AAA** | Todos os testes: `Arrange / Act / Assert` |
| **@DisplayName** | Cada teste explica o par/caminho coberto |
| **Mockito @Mock** | `VendaServiceTest.java` — isolamento de dependências |
| **AssertJ fluente** | `isEqualByComparingTo()` para valores monetários |
| **JaCoCo** | Falha o build se cobertura < 85% |
| **Tags JUnit 5** | `@Tag("pairwise")`, `@Tag("complexidade-ciclomatica")`, `@Tag("integracao")` |

### No projeto `vendas-testes-tradicionais`

| Conceito | Onde ver |
|---|---|
| **Isolamento de parâmetros** | Cada arquivo de teste foca em 1 parâmetro |
| **Redundâncias anotadas** | `[⚠️ REDUNDANTE]` no `@DisplayName` |
| **Lacunas documentadas** | Comentários `// ✗ Nunca testado` ao final de cada classe |
| **Comparativo inline** | `BaseTesteTradicional.java` — tabela completa em Javadoc |

---

## 🔑 Conclusão

```
Pairwise Testing + Complexidade Ciclomática =
33 casos | 100% pares | 100% caminhos CC | 0 redundâncias | rastreabilidade total

Abordagem Tradicional (ad-hoc) =
48 casos | 98% pares | 87,5% caminhos CC | 27% redundantes | zero rastreabilidade
```

> **Técnicas de redução não diminuem a qualidade — elas a aumentam.**
> Com método, testamos menos e descobrimos mais.

---

## 👨‍💻 Autores

Trabalho desenvolvido para a disciplina de **Laboratório de Verificação e Validação de Software**
— FATEC | Curso de Análise e Desenvolvimento de Sistemas | Novembro/2026
