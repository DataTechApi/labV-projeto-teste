package com.fatec.vendas.model;

/**
 * Enum que representa as regiões de entrega disponíveis.
 *
 * <p>Parâmetro de entrada no Pairwise Testing.
 * Influencia o valor do frete calculado pelo FreteService.</p>
 *
 * <ul>
 *   <li>SUDESTE  — frete grátis acima de R$ 300</li>
 *   <li>NORDESTE — frete fixo com desconto parcial</li>
 *   <li>NORTE    — frete mais alto, sem desconto automático</li>
 * </ul>
 */
public enum RegiaoEntrega {
    SUDESTE,
    NORDESTE,
    NORTE
}
