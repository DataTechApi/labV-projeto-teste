package com.fatec.vendas.service;

import com.fatec.vendas.exception.VendaException;
import com.fatec.vendas.model.Venda;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serviço principal de orquestração do processamento de uma venda.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * ANÁLISE DE COMPLEXIDADE CICLOMÁTICA — processarVenda()
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Grafo de Fluxo de Controle (simplificado):
 *
 *   [N1] início
 *     │
 *     ▼
 *   [N2] venda == null ? ─── Sim ──▶ [N3] lançar exceção
 *     │ Não
 *     ▼
 *   [N4] itens vazio ? ──── Sim ──▶ [N5] lançar exceção
 *     │ Não
 *     ▼
 *   [N6] calcular valorBruto
 *     │
 *     ▼
 *   [N7] valorBruto ≤ 0 ? ── Sim ──▶ [N8] lançar exceção
 *     │ Não
 *     ▼
 *   [N9] calcular desconto (DescontoService)
 *     │
 *     ▼
 *   [N10] calcular frete (FreteService)
 *     │
 *     ▼
 *   [N11] aplicar desconto e somar frete
 *     │
 *     ▼
 *   [N12] valorFinal < 0 ? ─ Sim ──▶ [N13] valorFinal = 0
 *     │ Não
 *     ▼
 *   [N14] salvar venda
 *     │
 *     ▼
 *   [N15] retorno
 *
 * Nós (N)   = 15
 * Arestas(E) = 18
 * Componentes Conexos (P) = 1
 *
 * CC = E - N + 2×P = 18 - 15 + 2 = 5
 *
 * (Incluindo as exceções como nós terminais, CC real = 5+2 lançamentos = 7)
 *
 * Caminhos Independentes:
 *   P1 — venda null → VendaException
 *   P2 — itens vazio → VendaException
 *   P3 — valorBruto ≤ 0 → VendaException
 *   P4 — fluxo normal, valorFinal > 0
 *   P5 — fluxo normal, valorFinal < 0 (impossível com dados válidos, proteção defensiva)
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class VendaService {

    private final DescontoService descontoService;
    private final FreteService    freteService;

    public VendaService(DescontoService descontoService, FreteService freteService) {
        this.descontoService = descontoService;
        this.freteService    = freteService;
    }

    /**
     * Processa uma venda aplicando descontos, frete e validações de negócio.
     *
     * <p>Fluxo:</p>
     * <ol>
     *   <li>Valida os campos obrigatórios da venda</li>
     *   <li>Calcula o valor bruto somando os itens</li>
     *   <li>Calcula o percentual de desconto via {@link DescontoService}</li>
     *   <li>Calcula o frete via {@link FreteService}</li>
     *   <li>Aplica desconto e soma o frete para obter o valor final</li>
     * </ol>
     *
     * @param venda venda a ser processada (deve ter cliente, forma de pagamento, região e itens)
     * @return a mesma venda com valorBruto, desconto, frete e valorFinal preenchidos
     * @throws VendaException se a venda for inválida
     */
    public Venda processarVenda(Venda venda) {

        // ── [N2] Validação: venda nula ─────────────────────────────────────
        if (venda == null) {                                           // E1 — P1
            throw new VendaException("Venda não pode ser nula");
        }

        // ── [N4] Validação: sem itens ──────────────────────────────────────
        if (venda.getItens() == null || venda.getItens().isEmpty()) { // E3 — P2
            throw new VendaException("A venda deve conter pelo menos um item");
        }

        // ── [N6] Calcula valor bruto ───────────────────────────────────────
        BigDecimal valorBruto = venda.calcularValorBruto();
        venda.setValorBruto(valorBruto);

        // ── [N7] Validação: valor inválido ─────────────────────────────────
        if (valorBruto.compareTo(BigDecimal.ZERO) <= 0) {             // E5 — P3
            throw new VendaException("Valor bruto da venda deve ser positivo");
        }

        // ── [N9] Calcula e aplica desconto ─────────────────────────────────
        BigDecimal percentualDesconto = descontoService.calcularDesconto(venda);
        venda.setDesconto(percentualDesconto);

        BigDecimal valorComDesconto = valorBruto
                .multiply(BigDecimal.ONE.subtract(percentualDesconto))
                .setScale(2, RoundingMode.HALF_UP);

        // ── [N10] Calcula e adiciona frete ─────────────────────────────────
        BigDecimal frete = freteService.calcularFrete(venda.getRegiao(), valorBruto);
        venda.setFrete(frete);

        // ── [N11] Valor final = (bruto × (1 - desconto)) + frete ──────────
        BigDecimal valorFinal = valorComDesconto.add(frete);

        // ── [N12] Proteção defensiva: valor final nunca negativo ───────────
        if (valorFinal.compareTo(BigDecimal.ZERO) < 0) {              // E11 — P5
            valorFinal = BigDecimal.ZERO;
        }

        venda.setValorFinal(valorFinal);
        return venda;
    }
}
