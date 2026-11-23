package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #2 — Foco na FORMA DE PAGAMENTO
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Abordagem ad-hoc: o desenvolvedor pensa "vou testar cada forma de pagamento".
 * Resultado: mais redundância, e o desconto por TIPO DE CLIENTE é ignorado
 * em quase todos os casos (todos usam COMUM).
 *
 * PROBLEMAS ACUMULADOS:
 *   - Par (VIP + BOLETO) NUNCA testado neste arquivo
 *   - Par (REVENDEDOR + CARTAO) NUNCA testado neste arquivo
 *   - Combinação de BOLETO com valor alto (onde taxa + bônus volume se interagem) NÃO é testada
 *   - Repete região SUDESTE em todos os casos sem perceber
 *
 * CONTAGEM DE REDUNDÂNCIAS ATÉ AQUI:
 *   - (COMUM + PIX + SUDESTE + R$50): testado no arquivo de Cliente E aqui = 2x
 *   - (COMUM + CARTÃO + SUDESTE + R$200): testado aqui E no arquivo de Região = 2x
 */
@DisplayName("[TRADICIONAL] Teste por Forma de Pagamento — abordagem ad-hoc isolada")
class TesteTradicionalPagamentoTest extends BaseTesteTradicional {

    @Test
    @DisplayName("Pagamento PIX com COMUM no SUDESTE — R$50 [⚠️ JÁ TESTADO em TipoClienteTest]")
    void pagamentoPix_comum_sudeste() {
        // ⚠️ REDUNDANTE: exatamente igual ao primeiro teste de TesteTradicionalTipoClienteTest.
        // O desenvolvedor não percebe que duplicou o caso.
        Venda resultado = vendaService.processarVenda(
            criarVenda(10L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        // PIX=5%, frete SUDESTE ≤ 300 = R$15 → 50 × 0.95 + 15 = R$62.50
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("62.50");
    }

    @Test
    @DisplayName("Pagamento CARTÃO com COMUM no SUDESTE — R$200")
    void pagamentoCartao_comum_sudeste() {
        // 0% desconto, frete SUDESTE ≤ 300 = R$15 → 200 + 15 = R$215.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(11L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("215.00");
    }

    @Test
    @DisplayName("Pagamento BOLETO com COMUM no SUDESTE — R$200")
    void pagamentoBoleto_comum_sudeste() {
        // ⚠️ Repete SUDESTE pela terceira vez. Nunca testa BOLETO com VIP ou REVENDEDOR.
        // BOLETO=-2%, mas COMUM sem bônus → desconto=0% (proteção), frete R$15
        // 200 × 1.00 + 15 = R$215.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(12L, TipoCliente.COMUM, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("215.00");
    }

    @Test
    @DisplayName("Pagamento PIX com VIP no SUDESTE — R$200 [⚠️ REDUNDANTE com TipoClienteTest extra]")
    void pagamentoPix_vip_sudeste_adicional() {
        // ⚠️ Semelhante ao clienteVip_pix_sudeste em TipoClienteTest, só muda o produto.
        // VIP=10% + PIX=5% = 15%, frete SUDESTE ≤ 300 = R$15
        // 200 × 0.85 + 15 = 170 + 15 = R$185.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(13L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("185.00");
    }

    @Test
    @DisplayName("Pagamento BOLETO com VIP no SUDESTE — R$200")
    void pagamentoBoleto_vip_sudeste() {
        // VIP=10% + BOLETO=-2% = 8%, frete SUDESTE ≤ 300 = R$15
        // 200 × 0.92 + 15 = 184 + 15 = R$199.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(14L, TipoCliente.VIP, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("199.00");
    }

    @Test
    @DisplayName("Pagamento CARTÃO com REVENDEDOR no SUDESTE — R$200 [⚠️ REDUNDANTE com TipoClienteTest]")
    void pagamentoCartao_revendedor_sudeste() {
        // ⚠️ REDUNDANTE com o caso adicional de TipoClienteTest (mesmo cliente, produto diferente).
        // REVENDEDOR=15%, frete SUDESTE ≤ 300 = R$15
        // 200 × 0.85 + 15 = 170 + 15 = R$185.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(15L, TipoCliente.REVENDEDOR, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("185.00");
    }

    // ── LACUNAS não percebidas: ───────────────────────────────────────────
    // ✗ (PIX + NORDESTE): nunca testado neste arquivo
    // ✗ (BOLETO + NORTE): nunca testado neste arquivo
    // ✗ (BOLETO + REVENDEDOR): a combinação com taxa+desconto composta não é coberta
    // ✗ (PIX + valor > R$500): interação com bônus de volume não é testada aqui
}
