package com.fatec.vendas.model;

/**
 * Representa um cliente do sistema de vendas.
 *
 * <p>O {@link TipoCliente} determina os descontos que serão aplicados
 * pelo {@code DescontoService} ao processar uma venda.</p>
 */
public class Cliente {

    private final Long id;
    private final String nome;
    private final TipoCliente tipo;

    public Cliente(Long id, String nome, TipoCliente tipo) {
        if (id == null)        throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        if (tipo == null)      throw new IllegalArgumentException("Tipo do cliente não pode ser nulo");

        this.id   = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    // ── Getters ──────────────────────────────────────────────────────────
    public Long getId()          { return id;   }
    public String getNome()      { return nome; }
    public TipoCliente getTipo() { return tipo; }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nome='" + nome + "', tipo=" + tipo + "}";
    }
}
