package com.fatec.vendas.model;

import java.math.BigDecimal;

/**
 * Representa um item (linha) dentro de uma venda.
 *
 * <p>Armazena o produto, a quantidade e o preço unitário no momento da venda
 * (snapshot de preço — evita que alterações futuras no produto afetem a venda).</p>
 */
public class ItemVenda {

    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoUnitario;

    public ItemVenda(Produto produto, int quantidade) {
        if (produto == null)   throw new IllegalArgumentException("Produto não pode ser nulo");
        if (quantidade <= 0)   throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (quantidade > produto.getEstoque())
            throw new IllegalArgumentException(
                "Estoque insuficiente para o produto: " + produto.getNome());

        this.produto        = produto;
        this.quantidade     = quantidade;
        this.precoUnitario  = produto.getPreco();
    }

    /**
     * Calcula o subtotal deste item (preço × quantidade).
     *
     * @return subtotal sem descontos
     */
    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public Produto getProduto()           { return produto;        }
    public int getQuantidade()            { return quantidade;     }
    public BigDecimal getPrecoUnitario()  { return precoUnitario;  }

    @Override
    public String toString() {
        return "ItemVenda{produto=" + produto.getNome()
             + ", qty=" + quantidade
             + ", precoUnit=" + precoUnitario
             + ", subtotal=" + calcularSubtotal() + "}";
    }
}
