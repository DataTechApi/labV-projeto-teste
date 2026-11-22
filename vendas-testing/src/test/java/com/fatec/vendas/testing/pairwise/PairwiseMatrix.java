package com.fatec.vendas.testing.pairwise;

/**
 * Matriz Pairwise para o sistema de Vendas.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TÉCNICA: PAIRWISE TESTING (All-Pairs Testing)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Objetivo: reduzir o número de casos de teste mantendo cobertura de todos
 * os PARES de valores possíveis entre os parâmetros de entrada.
 *
 * Parâmetros e valores:
 * ┌─────────────────────┬──────────────────────────────────────────┐
 * │ Parâmetro           │ Valores                                   │
 * ├─────────────────────┼──────────────────────────────────────────┤
 * │ P1: Tipo de cliente │ COMUM(C), VIP(V), REVENDEDOR(R)          │
 * │ P2: Forma pagamento │ PIX(X), CARTAO_CREDITO(T), BOLETO(B)     │
 * │ P3: Região entrega  │ SUDESTE(S), NORDESTE(N), NORTE(O)        │
 * │ P4: Faixa de valor  │ BAIXO<100(L), MEDIO 100-500(M), ALTO>500 │
 * └─────────────────────┴──────────────────────────────────────────┘
 *
 * Sem pairwise: 3 × 3 × 3 × 3 = 81 combinações
 * Com pairwise: 9 casos de teste
 * Economia: (81 - 9) / 81 = ~89%
 *
 * Matriz gerada (algoritmo de cobertura de pares):
 *
 * ┌────┬────────────┬──────────────────┬──────────┬────────────┬──────────────────────────────┐
 * │ TC │ P1:Cliente │ P2:Pagamento     │ P3:Região│ P4:Valor   │ Pares cobertos               │
 * ├────┼────────────┼──────────────────┼──────────┼────────────┼──────────────────────────────┤
 * │  1 │ COMUM      │ PIX              │ SUDESTE  │ BAIXO      │ C-X, C-S, C-L, X-S, X-L, S-L│
 * │  2 │ COMUM      │ CARTAO_CREDITO   │ NORDESTE │ MEDIO      │ C-T, C-N, C-M, T-N, T-M, N-M│
 * │  3 │ COMUM      │ BOLETO           │ NORTE    │ ALTO       │ C-B, C-O, C-H, B-O, B-H, O-H│
 * │  4 │ VIP        │ PIX              │ NORDESTE │ ALTO       │ V-X, V-N, V-H, X-N, X-H, N-H│
 * │  5 │ VIP        │ CARTAO_CREDITO   │ NORTE    │ BAIXO      │ V-T, V-O, V-L, T-O, T-L, O-L│
 * │  6 │ VIP        │ BOLETO           │ SUDESTE  │ MEDIO      │ V-B, V-S, V-M, B-S, B-M, S-M│
 * │  7 │ REVENDEDOR │ PIX              │ NORTE    │ MEDIO      │ R-X, R-O, R-M, X-O, X-M, O-M│
 * │  8 │ REVENDEDOR │ CARTAO_CREDITO   │ SUDESTE  │ ALTO       │ R-T, R-S, R-H, T-S, T-H, S-H│
 * │  9 │ REVENDEDOR │ BOLETO           │ NORDESTE │ BAIXO      │ R-B, R-N, R-L, B-N, B-L, N-L│
 * └────┴────────────┴──────────────────┴──────────┴────────────┴──────────────────────────────┘
 *
 * Verificação de cobertura de pares:
 * Total de pares possíveis = C(4,2) × 3² = 6 × 9 = 54 pares
 * Todos os 54 pares estão cobertos pelos 9 casos acima.
 */
public final class PairwiseMatrix {

    private PairwiseMatrix() { /* classe utilitária */ }

    // ── Faixas de valor (P4) ───────────────────────────────────────────────
    /** Valor baixo: R$ 50,00 — representa faixa P4=BAIXO (< R$100) */
    public static final double VALOR_BAIXO  =  50.00;

    /** Valor médio: R$ 250,00 — representa faixa P4=MEDIO (R$100–500) */
    public static final double VALOR_MEDIO  = 250.00;

    /** Valor alto: R$ 750,00 — representa faixa P4=ALTO (> R$500) */
    public static final double VALOR_ALTO   = 750.00;

    /**
     * Retorna a descrição textual da matriz pairwise para fins de log/documentação.
     */
    public static String descricaoMatriz() {
        return """
                ┌────┬────────────┬──────────────────┬──────────┬────────────┐
                │ TC │ Cliente    │ Pagamento        │ Região   │ Valor      │
                ├────┼────────────┼──────────────────┼──────────┼────────────┤
                │  1 │ COMUM      │ PIX              │ SUDESTE  │ BAIXO      │
                │  2 │ COMUM      │ CARTAO_CREDITO   │ NORDESTE │ MEDIO      │
                │  3 │ COMUM      │ BOLETO           │ NORTE    │ ALTO       │
                │  4 │ VIP        │ PIX              │ NORDESTE │ ALTO       │
                │  5 │ VIP        │ CARTAO_CREDITO   │ NORTE    │ BAIXO      │
                │  6 │ VIP        │ BOLETO           │ SUDESTE  │ MEDIO      │
                │  7 │ REVENDEDOR │ PIX              │ NORTE    │ MEDIO      │
                │  8 │ REVENDEDOR │ CARTAO_CREDITO   │ SUDESTE  │ ALTO       │
                │  9 │ REVENDEDOR │ BOLETO           │ NORDESTE │ BAIXO      │
                └────┴────────────┴──────────────────┴──────────┴────────────┘
                """;
    }
}
