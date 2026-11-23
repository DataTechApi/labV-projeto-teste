package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #5 — Desconto ad-hoc SEM análise de Complexidade Ciclomática
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Abordagem: o desenvolvedor testa o desconto "do jeito que parece certo",
 * escolhendo combinações arbitrárias sem identificar os caminhos independentes.
 *
 * COMPARATIVO DIRETO COM CC:
 *
 *   Com Complexidade Ciclomática (projeto vendas-testing):
 *     CC = 8 → 8 caminhos identificados → 8 casos derivados sistematicamente.
 *     Resultado: 100% de cobertura de ramos, 0 caminhos perdidos.
 *
 *   Sem CC (este projeto):
 *     12 casos escritos de forma ad-hoc.
 *     Resultado: 3 redundâncias, 1 caminho crítico perdido (P1 — boleto zerado).
 *
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │ Caminho │ Com CC │ Sem CC (este arquivo)                            │
 * ├─────────────────────────────────────────────────────────────────────┤
 * │ P1 (boleto zerado a 0%)    │ ✅ 1 caso  │ ❌ NÃO COBERTO           │
 * │ P2 (0% sem bonus)          │ ✅ 1 caso  │ ✅ mas repetido 3x       │
 * │ P3 (só PIX)                │ ✅ 1 caso  │ ✅                       │
 * │ P4 (PIX + volume)          │ ✅ 1 caso  │ ✅                       │
 * │ P5 (só VIP)                │ ✅ 1 caso  │ ✅                       │
 * │ P6 (VIP+PIX+volume)        │ ✅ 1 caso  │ ✅ mas junto com P4      │
 * │ P7 (REVENDEDOR+PIX)        │ ✅ 1 caso  │ ✅                       │
 * │ P8 (teto 25%)              │ ✅ 1 caso  │ ✅                       │
 * └─────────────────────────────────────────────────────────────────────┘
 */
@DisplayName("[TRADICIONAL] Teste de Desconto — SEM análise de Complexidade Ciclomática")
class TesteTradicionalDescontoTest extends BaseTesteTradicional {

    @Test
    @DisplayName("Sem desconto — COMUM + CARTÃO + R$50")
    void semDesconto_comum_cartao_baixo() {
        // ⚠️ REDUNDANTE: exatamente igual a casos em Pagamento e Região tests.
        Venda v = vendaService.processarVenda(
            criarVenda(40L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.0000");
    }

    @Test
    @DisplayName("Sem desconto — COMUM + CARTÃO + R$200 [⚠️ REDUNDANTE]")
    void semDesconto_comum_cartao_medio() {
        // ⚠️ REDUNDANTE com o anterior — apenas muda o produto.
        // O testador não sabe que o desconto 0% já foi coberto.
        Venda v = vendaService.processarVenda(
            criarVenda(41L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.0000");
    }

    @Test
    @DisplayName("Desconto VIP — VIP + CARTÃO + R$50")
    void desconto_vip_cartao_baixo() {
        Venda v = vendaService.processarVenda(
            criarVenda(42L, TipoCliente.VIP, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.1000");
    }

    @Test
    @DisplayName("Desconto REVENDEDOR — REVENDEDOR + CARTÃO + R$50")
    void desconto_revendedor_cartao_baixo() {
        Venda v = vendaService.processarVenda(
            criarVenda(43L, TipoCliente.REVENDEDOR, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.1500");
    }

    @Test
    @DisplayName("Desconto PIX — COMUM + PIX + R$50 [⚠️ REDUNDANTE 4x neste projeto]")
    void desconto_pix_comum_baixo() {
        // ⚠️ A QUARTA repetição deste caso em todo o projeto de testes tradicionais!
        Venda v = vendaService.processarVenda(
            criarVenda(44L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.0500");
    }

    @Test
    @DisplayName("Desconto VIP + PIX — VIP + PIX + R$50")
    void desconto_vip_pix_baixo() {
        // VIP=10% + PIX=5% = 15%
        Venda v = vendaService.processarVenda(
            criarVenda(45L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.1500");
    }

    @Test
    @DisplayName("Desconto REVENDEDOR + PIX — REVENDEDOR + PIX + R$50")
    void desconto_revendedor_pix_baixo() {
        // REVENDEDOR=15% + PIX=5% = 20%
        Venda v = vendaService.processarVenda(
            criarVenda(46L, TipoCliente.REVENDEDOR, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.2000");
    }

    @Test
    @DisplayName("Desconto com volume — COMUM + PIX + R$600")
    void desconto_volume_comum_pix_alto() {
        // PIX=5% + volume>500=8% = 13%
        Venda v = vendaService.processarVenda(
            criarVenda(47L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoC));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.1300");
    }

    @Test
    @DisplayName("Desconto VIP + PIX + volume — VIP + PIX + R$600")
    void desconto_vip_pix_alto() {
        // VIP=10% + PIX=5% + volume>500=8% = 23%
        Venda v = vendaService.processarVenda(
            criarVenda(48L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoC));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.2300");
    }

    @Test
    @DisplayName("Teto 25% — REVENDEDOR + PIX + R$600")
    void desconto_teto25pct_revendedor_pix_alto() {
        // REVENDEDOR=15% + PIX=5% + volume>500=8% = 28% → teto 25%
        Venda v = vendaService.processarVenda(
            criarVenda(49L, TipoCliente.REVENDEDOR, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoC));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.2500");
    }

    @Test
    @DisplayName("VIP + BOLETO + R$200 — desconto composto")
    void desconto_vip_boleto_medio() {
        // VIP=10% + BOLETO=-2% = 8%
        Venda v = vendaService.processarVenda(
            criarVenda(50L, TipoCliente.VIP, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.0800");
    }

    @Test
    @DisplayName("REVENDEDOR + BOLETO + R$200 — desconto composto")
    void desconto_revendedor_boleto_medio() {
        // REVENDEDOR=15% + BOLETO=-2% = 13%
        Venda v = vendaService.processarVenda(
            criarVenda(51L, TipoCliente.REVENDEDOR, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.1300");
    }

    // ── CAMINHO PERDIDO (não percebido sem CC): ────────────────────────────
    // ✗ P1: COMUM + BOLETO + baixo → desconto seria -2% mas deve ser zerado.
    //   O testador nunca testa COMUM + BOLETO com valor baixo e sem outros descontos,
    //   pois sempre que usa BOLETO, mistura com VIP ou REVENDEDOR (que mascaram o -2%).
    //   CONCLUSÃO: o caso onde o desconto negativo é zerado NUNCA É TESTADO.
}
