package com.fatec.vendas.service;

import com.fatec.vendas.model.FormaPagamento;
import com.fatec.vendas.model.TipoCliente;
import com.fatec.vendas.model.Venda;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serviço responsável por calcular o percentual de desconto de uma venda.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * ANÁLISE DE COMPLEXIDADE CICLOMÁTICA — calcularDesconto()
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Grafo de Fluxo de Controle:
 *
 *        [N1] início
 *          │
 *          ▼
 *        [N2] cliente == VIP ?────────── Sim ──▶ [N3] desconto += 10%
 *          │ Não                                        │
 *          │◀───────────────────────────────────────────┘
 *          │
 *          ▼
 *        [N4] cliente == REVENDEDOR ?─── Sim ──▶ [N5] desconto += 15%
 *          │ Não                                        │
 *          │◀───────────────────────────────────────────┘
 *          │
 *          ▼
 *        [N6] pagamento == PIX ?──────── Sim ──▶ [N7] desconto += 5%
 *          │ Não                                        │
 *          │◀───────────────────────────────────────────┘
 *          │
 *          ▼
 *        [N8] pagamento == BOLETO ?───── Sim ──▶ [N9] desconto -= 2%
 *          │ Não                                        │
 *          │◀───────────────────────────────────────────┘
 *          │
 *          ▼
 *       [N10] valorBruto > 500 ?──────── Sim ──▶ [N11] desconto += 8%
 *          │ Não                                        │
 *          │◀───────────────────────────────────────────┘
 *          │
 *          ▼
 *       [N12] desconto > 25% ?────────── Sim ──▶ [N13] desconto = 25%
 *          │ Não                                        │
 *          │◀───────────────────────────────────────────┘
 *          │
 *          ▼
 *       [N14] retorno
 *
 * Nós (N)   = 14
 * Arestas(E) = 20  (14 lineares + 6 desvios de if)
 * Componentes Conexos (P) = 1
 *
 * CC = E - N + 2×P
 * CC = 20 - 14 + 2×1
 * CC = 8  ← número mínimo de casos de teste para cobertura completa
 *
 * Caminhos Independentes:
 *   P1 — COMUM, BOLETO,        valor ≤ 100  → desconto = -2%  (teto a 0%)
 *   P2 — COMUM, CARTAO_CREDITO, valor ≤ 100  → desconto =  0%
 *   P3 — COMUM, PIX,           valor ≤ 100  → desconto =  5%
 *   P4 — COMUM, PIX,           valor > 500  → desconto = 13%
 *   P5 — VIP,   CARTAO_CREDITO, valor ≤ 100  → desconto = 10%
 *   P6 — VIP,   PIX,           valor > 500  → desconto = 23%
 *   P7 — REVENDEDOR, PIX,      valor ≤ 100  → desconto = 20%
 *   P8 — REVENDEDOR, PIX,      valor > 500  → desconto = 25% (teto atingido: 15+5+8=28→25)
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class DescontoService {

    /** Teto máximo de desconto permitido. */
    public static final BigDecimal DESCONTO_MAXIMO = new BigDecimal("0.25");

    /**
     * Calcula o percentual de desconto a aplicar sobre o valor bruto da venda.
     *
     * <p>As regras são acumulativas:</p>
     * <ol>
     *   <li>VIP → +10%</li>
     *   <li>REVENDEDOR → +15%</li>
     *   <li>PIX → +5%</li>
     *   <li>BOLETO → -2% (taxa)</li>
     *   <li>Valor > R$ 500 → +8%</li>
     *   <li>Teto de 25% (limite máximo)</li>
     * </ol>
     *
     * @param venda venda com cliente, forma de pagamento e valor bruto já definidos
     * @return percentual de desconto entre 0.00 e 0.25 (BigDecimal)
     */
    public BigDecimal calcularDesconto(Venda venda) {
        BigDecimal desconto = BigDecimal.ZERO;

        // ── Regra 1: Desconto por tipo de cliente ─────────────────────────
        // [N2] Ramo: cliente VIP
        if (venda.getCliente().getTipo() == TipoCliente.VIP) {        // E1
            desconto = desconto.add(new BigDecimal("0.10"));           // N3
        }

        // [N4] Ramo: cliente REVENDEDOR
        if (venda.getCliente().getTipo() == TipoCliente.REVENDEDOR) { // E3
            desconto = desconto.add(new BigDecimal("0.15"));           // N5
        }

        // ── Regra 2: Desconto por forma de pagamento ──────────────────────
        // [N6] Ramo: PIX
        if (venda.getFormaPagamento() == FormaPagamento.PIX) {         // E5
            desconto = desconto.add(new BigDecimal("0.05"));           // N7
        }

        // [N8] Ramo: BOLETO (taxa)
        if (venda.getFormaPagamento() == FormaPagamento.BOLETO) {      // E7
            desconto = desconto.subtract(new BigDecimal("0.02"));      // N9
        }

        // ── Regra 3: Desconto por valor do pedido ─────────────────────────
        // [N10] Ramo: valor acima de R$ 500
        if (venda.getValorBruto().compareTo(new BigDecimal("500")) > 0) { // E9
            desconto = desconto.add(new BigDecimal("0.08"));              // N11
        }

        // ── Regra 4: Teto máximo de 25% ──────────────────────────────────
        // [N12] Ramo: desconto ultrapassou o teto
        if (desconto.compareTo(DESCONTO_MAXIMO) > 0) {                // E11
            desconto = DESCONTO_MAXIMO;                                // N13
        }

        // Garantir que desconto não seja negativo (BOLETO com sem outros descontos)
        if (desconto.compareTo(BigDecimal.ZERO) < 0) {
            desconto = BigDecimal.ZERO;
        }

        return desconto.setScale(4, RoundingMode.HALF_UP);
    }
}
