package com.fatec.vendas.testing.ciclomatica;

import com.fatec.vendas.model.RegiaoEntrega;
import com.fatec.vendas.service.FreteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes baseados em COMPLEXIDADE CICLOMÁTICA para o {@link FreteService}.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * CC = E - N + 2×P = 15 - 12 + 2 = 5
 *
 * Caminhos independentes:
 *   P1 — SUDESTE + valorBruto > 300  → frete = R$  0,00 (grátis)
 *   P2 — SUDESTE + valorBruto ≤ 300  → frete = R$ 15,00
 *   P3 — NORDESTE + valorBruto > 150 → frete = R$ 20,00
 *   P4 — NORDESTE + valorBruto ≤ 150 → frete = R$ 35,00
 *   P5 — NORTE                       → frete = R$ 45,00
 * ═══════════════════════════════════════════════════════════════════════════
 */
@Tag("complexidade-ciclomatica")
@DisplayName("Complexidade Ciclomática — FreteService (CC=5)")
class FreteServiceTest {

    private FreteService freteService;

    @BeforeEach
    void setUp() {
        freteService = new FreteService();
    }

    // ══════════════════════════════════════════════════════════════════════
    // P1 — SUDESTE + valor > R$300 → frete GRÁTIS
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P1: SUDESTE + R$500 → frete grátis (R$0,00)")
    void tc_p1_sudeste_valor_alto_frete_gratis() {
        BigDecimal frete = freteService.calcularFrete(RegiaoEntrega.SUDESTE, new BigDecimal("500.00"));

        assertThat(frete).isEqualByComparingTo("0.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P2 — SUDESTE + valor ≤ R$300 → frete R$15,00
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P2: SUDESTE + R$100 → frete R$15,00")
    void tc_p2_sudeste_valor_baixo_frete_padrao() {
        BigDecimal frete = freteService.calcularFrete(RegiaoEntrega.SUDESTE, new BigDecimal("100.00"));

        assertThat(frete).isEqualByComparingTo("15.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P3 — NORDESTE + valor > R$150 → frete R$20,00
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P3: NORDESTE + R$200 → frete R$20,00 (desconto nordeste)")
    void tc_p3_nordeste_valor_medio_frete_desconto() {
        BigDecimal frete = freteService.calcularFrete(RegiaoEntrega.NORDESTE, new BigDecimal("200.00"));

        assertThat(frete).isEqualByComparingTo("20.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P4 — NORDESTE + valor ≤ R$150 → frete R$35,00
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P4: NORDESTE + R$50 → frete R$35,00 (cheio)")
    void tc_p4_nordeste_valor_baixo_frete_cheio() {
        BigDecimal frete = freteService.calcularFrete(RegiaoEntrega.NORDESTE, new BigDecimal("50.00"));

        assertThat(frete).isEqualByComparingTo("35.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // P5 — NORTE → sempre R$45,00
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("P5: NORTE (qualquer valor) → frete fixo R$45,00")
    void tc_p5_norte_frete_fixo() {
        BigDecimal freteValorBaixo = freteService.calcularFrete(RegiaoEntrega.NORTE, new BigDecimal("30.00"));
        BigDecimal freteValorAlto  = freteService.calcularFrete(RegiaoEntrega.NORTE, new BigDecimal("1000.00"));

        assertThat(freteValorBaixo).isEqualByComparingTo("45.00");
        assertThat(freteValorAlto).isEqualByComparingTo("45.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Teste de fronteira: exatamente R$300 (limite SUDESTE grátis)
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Fronteira: SUDESTE + exatamente R$300 → frete R$15 (não é grátis)")
    void tc_fronteira_sudeste_exatamente_300() {
        BigDecimal frete = freteService.calcularFrete(RegiaoEntrega.SUDESTE, new BigDecimal("300.00"));

        // compareTo > 0 → não inclui o valor exato 300
        assertThat(frete).isEqualByComparingTo("15.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Teste de entrada inválida: região nula
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Erro: região nula → IllegalArgumentException")
    void tc_erro_regiao_nula() {
        assertThatThrownBy(() -> freteService.calcularFrete(null, new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Região não pode ser nula");
    }
}
