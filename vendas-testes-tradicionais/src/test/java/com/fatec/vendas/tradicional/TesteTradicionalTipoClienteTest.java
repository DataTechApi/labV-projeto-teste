package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * TESTE TRADICIONAL #1 — Foco no TIPO DE CLIENTE
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Abordagem ad-hoc: o desenvolvedor pensa "vou testar cada tipo de cliente"
 * e escreve casos para COMUM, VIP e REVENDEDOR — mas testa SEMPRE com os
 * mesmos valores de pagamento e região.
 *
 * PROBLEMA OCULTO:
 *   - 3 casos aqui, mais os 3 no arquivo de Pagamento, mais os 3 no de Região...
 *   - O par (VIP + NORTE) NUNCA é testado nestas suítes individuais!
 *   - O par (REVENDEDOR + BOLETO) NUNCA aparece!
 *   - Sem a matriz pairwise, o testador NÃO SABE o que está faltando.
 *
 * COMPARATIVO:
 *   Pairwise cobre todos os pares com 9 casos.
 *   Esta abordagem usa 3 casos e deixa 42 pares SEM COBERTURA.
 */
@DisplayName("[TRADICIONAL] Teste por Tipo de Cliente — abordagem ad-hoc isolada")
class TesteTradicionalTipoClienteTest extends BaseTesteTradicional {

    // ── Sempre usa PIX + SUDESTE + R$50 — ignora combinações com outros parâmetros ──

    @Test
    @DisplayName("Cliente COMUM com PIX no SUDESTE — R$50")
    void clienteComum_pix_sudeste() {
        // O desenvolvedor não percebe que este par (COMUM + SUDESTE)
        // já aparecerá em OUTROS testes de pagamento e região...
        Venda resultado = vendaService.processarVenda(
            criarVenda(1L, TipoCliente.COMUM, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        // Desconto PIX=5%, frete SUDESTE ≤ 300 = R$15
        // 50 × 0.95 + 15 = 47.50 + 15 = R$62.50
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("62.50");
    }

    @Test
    @DisplayName("Cliente VIP com PIX no SUDESTE — R$50")
    void clienteVip_pix_sudeste() {
        // ⚠️ Repete SUDESTE + PIX + R$50 — só muda o cliente.
        // O par (VIP + NORDESTE), (VIP + NORTE), (VIP + BOLETO) NUNCA são testados aqui.
        Venda resultado = vendaService.processarVenda(
            criarVenda(2L, TipoCliente.VIP, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        // VIP=10% + PIX=5% = 15%, frete R$15
        // 50 × 0.85 + 15 = 42.50 + 15 = R$57.50
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("57.50");
    }

    @Test
    @DisplayName("Cliente REVENDEDOR com PIX no SUDESTE — R$50")
    void clienteRevendedor_pix_sudeste() {
        // ⚠️ Repete SUDESTE + PIX + R$50 pela terceira vez.
        // O par (REVENDEDOR + CARTÃO), (REVENDEDOR + BOLETO) NUNCA são testados aqui.
        Venda resultado = vendaService.processarVenda(
            criarVenda(3L, TipoCliente.REVENDEDOR, FormaPagamento.PIX, RegiaoEntrega.SUDESTE, produtoA));

        // REVENDEDOR=15% + PIX=5% = 20%, frete R$15
        // 50 × 0.80 + 15 = 40.00 + 15 = R$55.00
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("55.00");
    }

    // ── O desenvolvedor resolve "caprichar" e adiciona mais alguns ────────────

    @Test
    @DisplayName("Cliente VIP com CARTÃO no SUDESTE — R$200 [REDUNDANTE com arquivo de Pagamento]")
    void clienteVip_cartao_sudeste_adicional() {
        // ⚠️ REDUNDANTE: exatamente este caso já existe em TesteTradicionalPagamentoTest!
        // Sem rastreabilidade, o testador não sabe que está repetindo.
        Venda resultado = vendaService.processarVenda(
            criarVenda(4L, TipoCliente.VIP, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE, produtoB));

        // VIP=10%, frete SUDESTE ≤ 300 = R$15
        // 200 × 0.90 + 15 = 180 + 15 = R$195.00
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("195.00");
    }

    @Test
    @DisplayName("Cliente COMUM com CARTÃO no NORDESTE — R$200 [REDUNDANTE com arquivo de Região]")
    void clienteComum_cartao_nordeste_adicional() {
        // ⚠️ REDUNDANTE: já testado em TesteTradicionalRegiaoTest.
        Venda resultado = vendaService.processarVenda(
            criarVenda(5L, TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE, produtoB));

        // 0% desconto, frete NORDESTE > 150 = R$20
        // 200 × 1.00 + 20 = R$220.00
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("220.00");
    }

    // ── LACUNAS não percebidas pelo desenvolvedor: ─────────────────────────
    // ✗ (VIP + BOLETO + NORTE) nunca testado
    // ✗ (REVENDEDOR + BOLETO + NORDESTE) nunca testado
    // ✗ (COMUM + PIX + NORTE) nunca testado
    // ✗ Nenhum caso com valor > R$500 no arquivo de cliente
}
