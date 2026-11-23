package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #7 — Combinações "Aleatórias" Escolhidas pelo Desenvolvedor
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Fase final: o desenvolvedor tenta "cobrir combinações" mas sem método.
 * Pega as que "parecem importantes" ou "que lembraram" na hora de escrever.
 *
 * RESULTADO:
 *   - Mais casos redundantes com os anteriores
 *   - Pares que ficaram de fora continuam de fora
 *   - Total do projeto sobe para 55 casos (vs 9 do Pairwise)
 *   - Cobertura de pares: ~72% (39 de 54 pares)
 *   - Caminhos CC cobertos: 6 de 8 do DescontoService, 3 de 5 do FreteService
 *
 * PARES AINDA SEM COBERTURA APÓS TODOS OS TESTES TRADICIONAIS:
 *   (COMUM + BOLETO + BAIXO) sem outros descontos → P1 DescontoService
 *   (VIP + NORTE + BOLETO)
 *   (NORDESTE + BAIXO) → P4 FreteService
 *   (SUDESTE + ALTO via frete grátis explícito) → P1 FreteService
 */
@DisplayName("[TRADICIONAL] Combinações Ad-Hoc Finais — acumulando mais redundâncias")
class TesteTradicionalCombinacoesTest extends BaseTesteTradicional {

    @Test
    @DisplayName("VIP + PIX + NORDESTE + R$600 [⚠️ REDUNDANTE com ValorTest]")
    void vip_pix_nordeste_alto() {
        // ⚠️ Exatamente igual a TesteTradicionalValorTest.valor_alto_vip_pix_nordeste
        Venda v = vendaService.processarVenda(
            criarVenda(70L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.NORDESTE, produtoC));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.2300");
        assertThat(v.getFrete()).isEqualByComparingTo("20.00");
        assertThat(v.getValorFinal()).isEqualByComparingTo("482.00");
    }

    @Test
    @DisplayName("COMUM + PIX + NORTE + R$600 — novo par NORTE+ALTO")
    void comum_pix_norte_alto() {
        // Novo par (NORTE + ALTO), mas (COMUM + NORTE) já foi testado em RegiaoTest.
        // PIX=5% + volume>500=8% = 13%, frete NORTE = R$45
        // 600 × 0.87 + 45 = 522 + 45 = R$567.00
        Venda v = vendaService.processarVenda(
            criarVenda(71L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.NORTE, produtoC));

        assertThat(v.getFrete()).isEqualByComparingTo("45.00");
        assertThat(v.getValorFinal()).isEqualByComparingTo("567.00");
    }

    @Test
    @DisplayName("REVENDEDOR + CARTÃO + NORDESTE + R$50")
    void revendedor_cartao_nordeste_baixo() {
        // REVENDEDOR=15%, frete NORDESTE ≤ 150 = R$35
        // 50 × 0.85 + 35 = 42.50 + 35 = R$77.50
        Venda v = vendaService.processarVenda(
            criarVenda(72L, TipoCliente.REVENDEDOR, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE, produtoA));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.1500");
        assertThat(v.getFrete()).isEqualByComparingTo("35.00"); // ← P4 do FreteService coberto por acidente!
        assertThat(v.getValorFinal()).isEqualByComparingTo("77.50");
    }

    @Test
    @DisplayName("VIP + BOLETO + NORTE + R$200")
    void vip_boleto_norte_medio() {
        // VIP=10% + BOLETO=-2% = 8%, frete NORTE = R$45
        // 200 × 0.92 + 45 = 184 + 45 = R$229.00
        Venda v = vendaService.processarVenda(
            criarVenda(73L, TipoCliente.VIP, FormaPagamento.BOLETO, RegiaoEntrega.NORTE, produtoB));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.0800");
        assertThat(v.getFrete()).isEqualByComparingTo("45.00");
        assertThat(v.getValorFinal()).isEqualByComparingTo("229.00");
    }

    @Test
    @DisplayName("COMUM + CARTÃO + NORTE + R$600")
    void comum_cartao_norte_alto() {
        // 0% + volume>500=8% = 8%, frete NORTE = R$45
        // 600 × 0.92 + 45 = 552 + 45 = R$597.00
        Venda v = vendaService.processarVenda(
            criarVenda(74L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORTE, produtoC));

        assertThat(v.getValorFinal()).isEqualByComparingTo("597.00");
    }

    @Test
    @DisplayName("REVENDEDOR + BOLETO + SUDESTE + R$600")
    void revendedor_boleto_sudeste_alto() {
        // REVENDEDOR=15% + BOLETO=-2% + volume>500=8% = 21%
        // frete SUDESTE > 300 = R$0
        // 600 × 0.79 + 0 = R$474.00
        Venda v = vendaService.processarVenda(
            criarVenda(75L, TipoCliente.REVENDEDOR, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE, produtoC));

        assertThat(v.getDesconto()).isEqualByComparingTo("0.2100");
        assertThat(v.getFrete()).isEqualByComparingTo("0.00");
        assertThat(v.getValorFinal()).isEqualByComparingTo("474.00");
    }

    @Test
    @DisplayName("VIP + CARTÃO + NORDESTE + R$600")
    void vip_cartao_nordeste_alto() {
        // VIP=10% + volume>500=8% = 18%, frete NORDESTE > 150 = R$20
        // 600 × 0.82 + 20 = 492 + 20 = R$512.00
        Venda v = vendaService.processarVenda(
            criarVenda(76L, TipoCliente.VIP, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE, produtoC));

        assertThat(v.getValorFinal()).isEqualByComparingTo("512.00");
    }

    @Test
    @DisplayName("COMUM + BOLETO + SUDESTE + R$200 [⚠️ REDUNDANTE com PagamentoTest]")
    void comum_boleto_sudeste_medio() {
        // ⚠️ REDUNDANTE com TesteTradicionalPagamentoTest.pagamentoBoleto_comum_sudeste
        // 0% desconto (boleto zerado), frete R$15 → R$215.00
        Venda v = vendaService.processarVenda(
            criarVenda(77L, TipoCliente.COMUM, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(v.getValorFinal()).isEqualByComparingTo("215.00");
    }

    // ── RESUMO FINAL DO PROJETO TRADICIONAL: ──────────────────────────────
    //
    // Total de casos de teste: 55
    // Casos REDUNDANTES identificados: ~15 (27%)
    //
    // Pares cobertos (de 54 possíveis):
    //   COMUM × PIX         ✅  VIP × PIX          ✅  REVENDEDOR × PIX   ✅
    //   COMUM × CARTÃO      ✅  VIP × CARTÃO        ✅  REVENDEDOR × CARTÃO ✅
    //   COMUM × BOLETO      ✅  VIP × BOLETO        ✅  REVENDEDOR × BOLETO ✅
    //   COMUM × SUDESTE     ✅  VIP × SUDESTE       ✅  REVENDEDOR × SUDESTE ✅
    //   COMUM × NORDESTE    ✅  VIP × NORDESTE      ✅  REVENDEDOR × NORDESTE ✅
    //   COMUM × NORTE       ✅  VIP × NORTE         ✅  REVENDEDOR × NORTE  ✅
    //   COMUM × BAIXO       ✅  VIP × BAIXO         ✅  REVENDEDOR × BAIXO  ✅
    //   COMUM × MEDIO       ✅  VIP × MEDIO         ✅  REVENDEDOR × MEDIO  ✅
    //   COMUM × ALTO        ✅  VIP × ALTO          ✅  REVENDEDOR × ALTO   ✅
    //   PIX × SUDESTE       ✅  CARTÃO × SUDESTE    ✅  BOLETO × SUDESTE    ✅
    //   PIX × NORDESTE      ✅  CARTÃO × NORDESTE   ✅  BOLETO × NORDESTE   ✅
    //   PIX × NORTE         ✅  CARTÃO × NORTE      ✅  BOLETO × NORTE      ✅
    //   PIX × BAIXO         ✅  CARTÃO × BAIXO      ✅  BOLETO × BAIXO      ❌ (nunca testado!)
    //   PIX × MEDIO         ✅  CARTÃO × MEDIO      ✅  BOLETO × MEDIO      ✅
    //   PIX × ALTO          ✅  CARTÃO × ALTO       ✅  BOLETO × ALTO       ✅
    //   SUDESTE × BAIXO     ✅  NORDESTE × BAIXO    ✅  NORTE × BAIXO       ✅
    //   SUDESTE × MEDIO     ✅  NORDESTE × MEDIO    ✅  NORTE × MEDIO       ✅
    //   SUDESTE × ALTO      ✅  NORDESTE × ALTO     ✅  NORTE × ALTO        ✅
    //
    // → 53 de 54 pares cobertos com 55 casos (Pairwise: 54/54 com 9 casos!)
    // → Caminho P1 do DescontoService (BOLETO zerado) AINDA NÃO COBERTO
}
