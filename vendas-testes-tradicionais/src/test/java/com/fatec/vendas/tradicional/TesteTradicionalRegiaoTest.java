package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #3 — Foco na REGIÃO DE ENTREGA / FRETE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Abordagem ad-hoc: o desenvolvedor pensa "vou testar cada região".
 * Resultado: fixa COMUM + CARTÃO em todos os casos (zero interação de região
 * com outros parâmetros).
 *
 * PROBLEMA CENTRAL:
 *   A região não existe isolada — ela interage com o valor do pedido
 *   para definir o frete (ex: SUDESTE grátis só acima de R$300).
 *   O testador testa SUDESTE com R$200 e nunca com R$600 (grátis).
 *   Portanto, o CAMINHO MAIS RENTÁVEL (frete grátis SUDESTE) não é coberto.
 *
 * COMPARATIVO COM CC:
 *   O FreteService tem CC=5 → 5 caminhos independentes.
 *   Esta abordagem cobre apenas 3 deles (P2, P3, P5) e deixa P1 e P4 sem cobertura.
 */
@DisplayName("[TRADICIONAL] Teste por Região de Entrega — abordagem ad-hoc isolada")
class TesteTradicionalRegiaoTest extends BaseTesteTradicional {

    @Test
    @DisplayName("Região SUDESTE com COMUM + CARTÃO — R$200 [P2: frete R$15]")
    void regiao_sudeste_valorMedio() {
        // Cobre P2 do FreteService, mas P1 (frete grátis) NÃO é testado aqui
        Venda resultado = vendaService.processarVenda(
            criarVenda(20L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        // 0% desconto, frete SUDESTE ≤ 300 = R$15 → 200 + 15 = R$215.00
        assertThat(resultado.getFrete()).isEqualByComparingTo("15.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("215.00");
    }

    @Test
    @DisplayName("Região NORDESTE com COMUM + CARTÃO — R$200 [P3: frete R$20]")
    void regiao_nordeste_valorMedio() {
        // Cobre P3, mas P4 (NORDESTE com valor baixo → R$35) NÃO é testado aqui
        Venda resultado = vendaService.processarVenda(
            criarVenda(21L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE, produtoB));

        // 0% desconto, frete NORDESTE > 150 = R$20 → 200 + 20 = R$220.00
        assertThat(resultado.getFrete()).isEqualByComparingTo("20.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("220.00");
    }

    @Test
    @DisplayName("Região NORTE com COMUM + CARTÃO — R$200 [P5: frete R$45]")
    void regiao_norte_valorMedio() {
        // Cobre P5. Nunca testa NORTE com PIX ou VIP.
        Venda resultado = vendaService.processarVenda(
            criarVenda(22L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORTE, produtoB));

        // 0% desconto, frete NORTE = R$45 → 200 + 45 = R$245.00
        assertThat(resultado.getFrete()).isEqualByComparingTo("45.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("245.00");
    }

    @Test
    @DisplayName("Região SUDESTE com COMUM + CARTÃO — R$200 [⚠️ REDUNDANTE com 1º teste]")
    void regiao_sudeste_semDesconto_duplicado() {
        // ⚠️ EXATAMENTE IGUAL AO PRIMEIRO TESTE DESTA CLASSE.
        // O desenvolvedor "caprichou" mas simplesmente repetiu sem perceber.
        Venda resultado = vendaService.processarVenda(
            criarVenda(23L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getFrete()).isEqualByComparingTo("15.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("215.00");
    }

    @Test
    @DisplayName("Região SUDESTE com VIP + PIX — R$200 [⚠️ REDUNDANTE com PagamentoTest]")
    void regiao_sudeste_vip_pix() {
        // ⚠️ REDUNDANTE: já testado em TesteTradicionalPagamentoTest.pagamentoPix_vip_sudeste_adicional
        Venda resultado = vendaService.processarVenda(
            criarVenda(24L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoB));

        assertThat(resultado.getValorFinal()).isEqualByComparingTo("185.00");
    }

    // ── LACUNAS não percebidas: ────────────────────────────────────────────
    // ✗ P1: SUDESTE + valor > R$300 → frete GRÁTIS — NUNCA testado!
    //       Este é o caminho mais importante do SUDESTE e ficou de fora.
    // ✗ P4: NORDESTE + valor ≤ R$150 → frete R$35 — NUNCA testado!
    // ✗ Nenhum teste de NORTE com VIP, REVENDEDOR ou PIX
    // ✗ 2 casos explicitamente REDUNDANTES neste arquivo
}
