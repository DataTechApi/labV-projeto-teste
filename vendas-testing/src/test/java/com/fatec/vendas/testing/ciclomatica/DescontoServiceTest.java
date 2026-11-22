package com.fatec.vendas.testing.ciclomatica;

import com.fatec.vendas.model.*;
import com.fatec.vendas.service.DescontoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes baseados em COMPLEXIDADE CICLOMÁTICA para o {@link DescontoService}.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TÉCNICA: COMPLEXIDADE CICLOMÁTICA (CC) de McCabe
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * CC = E - N + 2×P
 *
 * Onde:
 *   E = número de arestas no grafo de fluxo de controle
 *   N = número de nós
 *   P = número de componentes conexos (1 para método único)
 *
 * Para calcularDesconto():
 *   E = 20, N = 14, P = 1
 *   CC = 20 - 14 + 2 = 8
 *
 * Isso significa que são necessários no MÍNIMO 8 casos de teste para
 * cobrir todos os caminhos independentes do método.
 *
 * ─────────────────────────────────────────────────────────────────────────
 * Mapeamento Caminho → Caso de Teste:
 *
 * P1 (COMUM+BOLETO+BAIXO)   → tc_p1_desconto_negativo_vai_a_zero
 * P2 (COMUM+CARTAO+BAIXO)   → tc_p2_sem_desconto
 * P3 (COMUM+PIX+BAIXO)      → tc_p3_desconto_somente_pix
 * P4 (COMUM+PIX+ALTO)       → tc_p4_desconto_pix_mais_volume
 * P5 (VIP+CARTAO+BAIXO)     → tc_p5_desconto_somente_vip
 * P6 (VIP+PIX+ALTO)         → tc_p6_desconto_vip_pix_volume
 * P7 (REVENDEDOR+PIX+BAIXO) → tc_p7_desconto_revendedor_pix
 * P8 (REVENDEDOR+PIX+ALTO)  → tc_p8_desconto_atinge_teto_25pct
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Tag("complexidade-ciclomatica")
@DisplayName("Complexidade Ciclomática — DescontoService (CC=8)")
class DescontoServiceTest {

    private DescontoService descontoService;

    @BeforeEach
    void setUp() {
        descontoService = new DescontoService();
    }

    // ── Método auxiliar para criar venda com valor bruto customizado ───────
    private Venda criarVenda(TipoCliente tipoCliente, FormaPagamento pagamento, double valorBruto) {
        Cliente cliente = new Cliente(1L, "Teste", tipoCliente);
        Venda venda = new Venda(1L, cliente, pagamento, RegiaoEntrega.SUDESTE);
        venda.setValorBruto(BigDecimal.valueOf(valorBruto));
        return venda;
    }

    // ══════════════════════════════════════════════════════════════════════
    // P1 — Caminho: COMUM + BOLETO + valor ≤ 500
    //      Ramos executados: BOLETO(-2%), sem vip, sem revendedor, sem pix, sem alto
    //      Resultado esperado: desconto seria -2%, mas teto inferior = 0%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P1: COMUM + BOLETO + baixo valor → desconto = 0% (negativo zerado)")
    void tc_p1_desconto_negativo_vai_a_zero() {
        Venda venda = criarVenda(TipoCliente.COMUM, FormaPagamento.BOLETO, 50.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        // -2% fica zerado pela proteção
        assertThat(desconto).isEqualByComparingTo("0.0000");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P2 — Caminho: COMUM + CARTAO_CREDITO + valor ≤ 500
    //      Ramos executados: nenhum bônus/taxa
    //      Resultado esperado: desconto = 0%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P2: COMUM + CARTÃO + baixo valor → desconto = 0%")
    void tc_p2_sem_desconto() {
        Venda venda = criarVenda(TipoCliente.COMUM, FormaPagamento.CARTAO_CREDITO, 100.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        assertThat(desconto).isEqualByComparingTo("0.0000");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P3 — Caminho: COMUM + PIX + valor ≤ 500
    //      Ramos executados: PIX(+5%)
    //      Resultado esperado: desconto = 5%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P3: COMUM + PIX + baixo valor → desconto = 5%")
    void tc_p3_desconto_somente_pix() {
        Venda venda = criarVenda(TipoCliente.COMUM, FormaPagamento.PIX, 200.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        assertThat(desconto).isEqualByComparingTo("0.0500");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P4 — Caminho: COMUM + PIX + valor > 500
    //      Ramos executados: PIX(+5%) + ALTO(+8%)
    //      Resultado esperado: desconto = 13%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P4: COMUM + PIX + valor alto → desconto = 13%")
    void tc_p4_desconto_pix_mais_volume() {
        Venda venda = criarVenda(TipoCliente.COMUM, FormaPagamento.PIX, 600.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        assertThat(desconto).isEqualByComparingTo("0.1300");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P5 — Caminho: VIP + CARTAO_CREDITO + valor ≤ 500
    //      Ramos executados: VIP(+10%)
    //      Resultado esperado: desconto = 10%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P5: VIP + CARTÃO + baixo valor → desconto = 10%")
    void tc_p5_desconto_somente_vip() {
        Venda venda = criarVenda(TipoCliente.VIP, FormaPagamento.CARTAO_CREDITO, 100.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        assertThat(desconto).isEqualByComparingTo("0.1000");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P6 — Caminho: VIP + PIX + valor > 500
    //      Ramos executados: VIP(+10%) + PIX(+5%) + ALTO(+8%)
    //      Resultado esperado: desconto = 23%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P6: VIP + PIX + valor alto → desconto = 23%")
    void tc_p6_desconto_vip_pix_volume() {
        Venda venda = criarVenda(TipoCliente.VIP, FormaPagamento.PIX, 750.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        assertThat(desconto).isEqualByComparingTo("0.2300");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P7 — Caminho: REVENDEDOR + PIX + valor ≤ 500
    //      Ramos executados: REVENDEDOR(+15%) + PIX(+5%)
    //      Resultado esperado: desconto = 20%
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P7: REVENDEDOR + PIX + baixo valor → desconto = 20%")
    void tc_p7_desconto_revendedor_pix() {
        Venda venda = criarVenda(TipoCliente.REVENDEDOR, FormaPagamento.PIX, 200.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        assertThat(desconto).isEqualByComparingTo("0.2000");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P8 — Caminho: REVENDEDOR + PIX + valor > 500
    //      Ramos executados: REVENDEDOR(+15%) + PIX(+5%) + ALTO(+8%) = 28% → TETO 25%
    //      Resultado esperado: desconto = 25% (teto atingido)
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P8: REVENDEDOR + PIX + valor alto → teto de 25% atingido (28% → 25%)")
    void tc_p8_desconto_atinge_teto_25pct() {
        Venda venda = criarVenda(TipoCliente.REVENDEDOR, FormaPagamento.PIX, 800.0);

        BigDecimal desconto = descontoService.calcularDesconto(venda);

        // 15% + 5% + 8% = 28% → limitado a 25%
        assertThat(desconto).isEqualByComparingTo("0.2500");
        assertThat(desconto).isLessThanOrEqualTo(DescontoService.DESCONTO_MAXIMO);
    }
}
