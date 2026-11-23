package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #6 — Frete ad-hoc SEM análise de Complexidade Ciclomática
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Sem a análise de CC, o desenvolvedor não sabe que o FreteService tem 5 caminhos.
 * Ele escreve testes "até ficar satisfeito", sem método para saber quando parar.
 *
 * CAMINHOS DO FreteService (CC=5):
 *   P1 — SUDESTE + valor > R$300 → frete GRÁTIS   ← NUNCA testado!
 *   P2 — SUDESTE + valor ≤ R$300 → R$15           ← testado (3x redundante)
 *   P3 — NORDESTE + valor > R$150 → R$20           ← testado
 *   P4 — NORDESTE + valor ≤ R$150 → R$35           ← NUNCA testado!
 *   P5 — NORTE → R$45                              ← testado
 */
@DisplayName("[TRADICIONAL] Teste de Frete — SEM análise de Complexidade Ciclomática")
class TesteTradicionalFreteTest extends BaseTesteTradicional {

    @Test
    @DisplayName("Frete SUDESTE — R$50 → R$15 [⚠️ REDUNDANTE com RegiaoTest]")
    void frete_sudeste_valorBaixo() {
        // ⚠️ Já testado em TesteTradicionalRegiaoTest.regiao_sudeste_valorMedio
        Venda v = vendaService.processarVenda(
            criarVenda(60L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getFrete()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("Frete SUDESTE — R$200 → R$15 [⚠️ REDUNDANTE 3x neste projeto]")
    void frete_sudeste_valorMedio() {
        // ⚠️ TERCEIRA repetição do par SUDESTE+CARTAO+COMUM+valor_médio no projeto.
        Venda v = vendaService.processarVenda(
            criarVenda(61L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(v.getFrete()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("Frete NORDESTE — R$200 → R$20 [⚠️ REDUNDANTE com RegiaoTest]")
    void frete_nordeste_valorMedio() {
        // ⚠️ Já testado em TesteTradicionalRegiaoTest.regiao_nordeste_valorMedio
        Venda v = vendaService.processarVenda(
            criarVenda(62L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE, produtoB));

        assertThat(v.getFrete()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("Frete NORTE — R$200 → R$45 [⚠️ REDUNDANTE com RegiaoTest]")
    void frete_norte_qualquerValor() {
        // ⚠️ Já testado em TesteTradicionalRegiaoTest.regiao_norte_valorMedio
        Venda v = vendaService.processarVenda(
            criarVenda(63L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORTE, produtoB));

        assertThat(v.getFrete()).isEqualByComparingTo("45.00");
    }

    @Test
    @DisplayName("Frete SUDESTE com VIP — R$50 → R$15")
    void frete_sudeste_vip_baixo() {
        // O testador "diversifica" o cliente mas o frete já estava coberto.
        // Nenhum valor novo de frete é testado aqui.
        Venda v = vendaService.processarVenda(
            criarVenda(64L, TipoCliente.VIP, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getFrete()).isEqualByComparingTo("15.00");
    }

    @Test
    @DisplayName("Frete NORDESTE com VIP — R$200 → R$20")
    void frete_nordeste_vip_medio() {
        // Mesmo resultado de frete, apenas cliente diferente — redundante em termos de frete.
        Venda v = vendaService.processarVenda(
            criarVenda(65L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.NORDESTE, produtoB));

        assertThat(v.getFrete()).isEqualByComparingTo("20.00");
    }

    // ── CAMINHOS NUNCA TESTADOS (sem CC, o testador não sabe): ────────────
    // ✗ P1: SUDESTE + valor > R$300 → FRETE GRÁTIS (R$0)
    //   Este é o caminho mais economicamente relevante para o cliente final.
    //   O testador nunca passa de R$200 no SUDESTE. BUG aqui NUNCA seria detectado.
    //
    // ✗ P4: NORDESTE + valor ≤ R$150 → frete R$35
    //   O testador sempre usa R$200 no NORDESTE (que cai em P3).
    //   P4 permanece não testado.
}
