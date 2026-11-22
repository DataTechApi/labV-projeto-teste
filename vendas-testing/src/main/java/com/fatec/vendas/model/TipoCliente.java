package com.fatec.vendas.model;

/**
 * Enum que representa o tipo de cliente no sistema de vendas.
 *
 * <p>Usado no Pairwise Testing como um dos 4 parâmetros de entrada.
 * Cada tipo influencia diretamente o percentual de desconto aplicado.</p>
 *
 * <ul>
 *   <li>COMUM    — sem desconto por perfil</li>
 *   <li>VIP      — 10% de desconto adicional</li>
 *   <li>REVENDEDOR — 15% de desconto adicional</li>
 * </ul>
 */
public enum TipoCliente {
    COMUM,
    VIP,
    REVENDEDOR
}
