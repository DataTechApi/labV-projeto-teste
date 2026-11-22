package com.fatec.vendas.service;

import com.fatec.vendas.model.RegiaoEntrega;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serviço responsável por calcular o valor do frete de uma venda.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * ANÁLISE DE COMPLEXIDADE CICLOMÁTICA — calcularFrete()
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Grafo de Fluxo de Controle:
 *
 *        [N1] início
 *          │
 *          ▼
 *        [N2] regiao == SUDESTE ?
 *         Sim │                │ Não
 *             ▼                ▼
 *           [N3]             [N7] regiao == NORDESTE ?
 *   valorBruto > 300 ?        Sim │               │ Não (NORTE)
 *         Sim │  │ Não            ▼               ▼
 *             ▼  ▼              [N8]            [N10]
 *           [N4][N5]     valorBruto > 150 ?    frete = R$45
 *          R$0  R$15      Sim │   │ Não
 *             │   │           ▼   ▼
 *             └───┘         [N9] [N11]
 *               │          R$20  R$35
 *               │            │   │
 *               └─────┬──────┘   │
 *                     └──────────┘
 *                          │
 *                        [N12] retorno
 *
 * Nós (N)   = 12
 * Arestas(E) = 15
 * Componentes Conexos (P) = 1
 *
 * CC = E - N + 2×P = 15 - 12 + 2 = 5
 *
 * Caminhos Independentes:
 *   P1 — SUDESTE, valorBruto > 300  → frete = R$  0,00 (grátis)
 *   P2 — SUDESTE, valorBruto ≤ 300  → frete = R$ 15,00
 *   P3 — NORDESTE, valorBruto > 150 → frete = R$ 20,00
 *   P4 — NORDESTE, valorBruto ≤ 150 → frete = R$ 35,00
 *   P5 — NORTE                      → frete = R$ 45,00
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class FreteService {

    public static final BigDecimal FRETE_SUDESTE_GRATIS_MINIMO = new BigDecimal("300");
    public static final BigDecimal FRETE_NORDESTE_DESCONTO_MINIMO = new BigDecimal("150");

    public static final BigDecimal VALOR_SUDESTE_PADRAO  = new BigDecimal("15.00");
    public static final BigDecimal VALOR_NORDESTE_CHEIO  = new BigDecimal("35.00");
    public static final BigDecimal VALOR_NORDESTE_DESCONTO = new BigDecimal("20.00");
    public static final BigDecimal VALOR_NORTE           = new BigDecimal("45.00");

    /**
     * Calcula o valor do frete com base na região e no valor bruto do pedido.
     *
     * <p>Regras:</p>
     * <ul>
     *   <li>SUDESTE: grátis se valorBruto &gt; R$ 300; caso contrário R$ 15</li>
     *   <li>NORDESTE: R$ 20 se valorBruto &gt; R$ 150; caso contrário R$ 35</li>
     *   <li>NORTE: sempre R$ 45</li>
     * </ul>
     *
     * @param regiao     região de entrega
     * @param valorBruto valor bruto total dos itens
     * @return valor do frete em BigDecimal
     */
    public BigDecimal calcularFrete(RegiaoEntrega regiao, BigDecimal valorBruto) {
        if (regiao == null)      throw new IllegalArgumentException("Região não pode ser nula");
        if (valorBruto == null)  throw new IllegalArgumentException("Valor bruto não pode ser nulo");

        // ── [N2] SUDESTE ───────────────────────────────────────────────────
        if (regiao == RegiaoEntrega.SUDESTE) {                                 // E1
            // [N3] frete grátis acima de R$ 300
            if (valorBruto.compareTo(FRETE_SUDESTE_GRATIS_MINIMO) > 0) {      // E2
                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);      // N4 — P1
            }
            return VALOR_SUDESTE_PADRAO;                                       // N5 — P2
        }

        // ── [N7] NORDESTE ──────────────────────────────────────────────────
        if (regiao == RegiaoEntrega.NORDESTE) {                                // E5
            // [N8] desconto no frete acima de R$ 150
            if (valorBruto.compareTo(FRETE_NORDESTE_DESCONTO_MINIMO) > 0) {   // E6
                return VALOR_NORDESTE_DESCONTO;                                // N9 — P3
            }
            return VALOR_NORDESTE_CHEIO;                                       // N11 — P4
        }

        // ── [N10] NORTE (default) ──────────────────────────────────────────
        return VALOR_NORTE;                                                    // P5
    }
}
