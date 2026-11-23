package com.fatec.vendas.model;

/**
 * Enum que representa as formas de pagamento disponíveis.
 *
 * <p>Parâmetro de entrada no Pairwise Testing.
 * Influencia desconto e taxas aplicadas na venda.</p>
 *
 * <ul>
 *   <li>PIX           — 5% de desconto adicional</li>
 *   <li>CARTAO_CREDITO — sem acréscimo nem desconto</li>
 *   <li>BOLETO        — taxa de 2% aplicada</li>
 * </ul>
 */
public enum FormaPagamento {
    PIX,
    CARTAO_CREDITO,
    BOLETO
}
