package com.fatec.vendas.model;

import java.math.BigDecimal;

/**
 * Representa um produto disponível para venda.
 */
public class Produto {

    private final Long id;
    private final String nome;
    private final BigDecimal preco;
    private int estoque;

    public Produto(Long id, String nome, BigDecimal preco, int estoque) {
        if (id == null)         throw new IllegalArgumentException("ID do produto não pode ser nulo");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Preço deve ser positivo");
        if (estoque < 0)        throw new IllegalArgumentException("Estoque não pode ser negativo");

        this.id      = id;
        this.nome    = nome;
        this.preco   = preco;
        this.estoque = estoque;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public Long getId()          { return id;      }
    public String getNome()      { return nome;    }
    public BigDecimal getPreco() { return preco;   }
    public int getEstoque()      { return estoque; }

    public void setEstoque(int estoque) {
        if (estoque < 0) throw new IllegalArgumentException("Estoque não pode ser negativo");
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', preco=" + preco + ", estoque=" + estoque + "}";
    }
}
