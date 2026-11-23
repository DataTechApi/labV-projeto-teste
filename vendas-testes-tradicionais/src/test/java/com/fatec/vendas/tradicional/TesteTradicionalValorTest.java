package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #4 — Foco no VALOR DO PEDIDO / FAIXA DE VALOR
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Abordagem ad-hoc: o desenvolvedor pensa "vou testar diferentes valores".
 * Escolhe os valores que "parecem representativos" sem metodologia.
 *
 * PROBLEMA: o limiar de R$500 (que ativa +8% de desconto) é a fronteira
 * mais crítica, mas o testador:
 *   - Não usa análise de valor de fronteira
 *   - Não sabe que R$500 exato NÃO tem desconto (> 500, não >= 500)
 *   - Repete combinações de outros arquivos
 *
 * COMPARATIVO COM CC:
 *   O DescontoService tem CC=8 → 8 caminhos independentes.
 *   Esta abordagem testa valores variados mas não mapeia os caminhos.
 *   O caminho P8 (teto de 25%) é coberto por acidente, não por design.
 */
@DisplayName("[TRADICIONAL] Teste por Faixa de Valor — abordagem ad-hoc isolada")
class TesteTradicionalValorTest extends BaseTesteTradicional {

    @Test
    @DisplayName("Valor BAIXO — R$50 com COMUM + PIX + SUDESTE [⚠️ REDUNDANTE 3x neste projeto]")
    void valor_baixo_comum_pix_sudeste() {
        // ⚠️ TERCEIRA REPETIÇÃO deste caso exato no projeto.
        // Já apareceu em TipoClienteTest e PagamentoTest.
        Venda resultado = vendaService.processarVenda(
            criarVenda(30L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        assertThat(resultado.getValorBruto()).isEqualByComparingTo("50.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0500");
    }

    @Test
    @DisplayName("Valor MÉDIO — R$200 com COMUM + CARTÃO + SUDESTE [⚠️ REDUNDANTE em Região e Pgto]")
    void valor_medio_comum_cartao_sudeste() {
        // ⚠️ REDUNDANTE com TesteTradicionalRegiaoTest e TesteTradicionalPagamentoTest.
        Venda resultado = vendaService.processarVenda(
            criarVenda(31L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorBruto()).isEqualByComparingTo("200.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0000");
    }

    @Test
    @DisplayName("Valor ALTO — R$600 com COMUM + PIX + SUDESTE [novo caso]")
    void valor_alto_comum_pix_sudeste() {
        // Este é um caso novo, mas:
        // - Usa SUDESTE, então frete é grátis (NOVO! Mas o testador não sabe que cobriu P1 do FreteService)
        // - Não verifica explicitamente o frete grátis
        // - Não testa o bônus de volume com outros clientes
        Venda resultado = vendaService.processarVenda(
            criarVenda(32L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoC));

        // PIX=5% + volume>500=8% = 13%, frete SUDESTE > 300 = R$0
        // 600 × 0.87 + 0 = R$522.00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("600.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.1300");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("522.00");
    }

    @Test
    @DisplayName("Valor ALTO — R$600 com VIP + PIX + NORDESTE")
    void valor_alto_vip_pix_nordeste() {
        // VIP=10% + PIX=5% + volume>500=8% = 23%, frete NORDESTE > 150 = R$20
        // 600 × 0.77 + 20 = 462 + 20 = R$482.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(33L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.NORDESTE, produtoC));

        assertThat(resultado.getValorBruto()).isEqualByComparingTo("600.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2300");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("482.00");
    }

    @Test
    @DisplayName("Valor ALTO — R$600 com REVENDEDOR + PIX + NORTE [teto 25%]")
    void valor_alto_revendedor_pix_norte() {
        // O testador "acidentalmente" cobre o teto de 25%:
        // REVENDEDOR=15% + PIX=5% + volume>500=8% = 28% → teto 25%
        // 600 × 0.75 + 45 = 450 + 45 = R$495.00
        // Mas sem a análise de CC, NÃO SABE que cobriu o caminho P8!
        Venda resultado = vendaService.processarVenda(
            criarVenda(34L, TipoCliente.REVENDEDOR, FormaPagamento.PIX, RegiaoEntrega.NORTE, produtoC));

        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2500"); // teto atingido
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("495.00");
    }

    @Test
    @DisplayName("Valor MÉDIO — R$200 com VIP + BOLETO + NORDESTE")
    void valor_medio_vip_boleto_nordeste() {
        // VIP=10% + BOLETO=-2% = 8%, frete NORDESTE > 150 = R$20
        // 200 × 0.92 + 20 = 184 + 20 = R$204.00
        Venda resultado = vendaService.processarVenda(
            criarVenda(35L, TipoCliente.VIP, FormaPagamento.BOLETO, RegiaoEntrega.NORDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("204.00");
    }

    // ── LACUNAS não percebidas: ────────────────────────────────────────────
    // ✗ Nenhum teste com exatamente R$500 (fronteira crítica)
    // ✗ Nenhum teste com R$301 (fronteira do frete grátis SUDESTE)
    // ✗ Nenhum teste com exatamente R$150 (fronteira do NORDESTE)
    // ✗ 2 casos REDUNDANTES neste arquivo com outros arquivos
}
