package com.fatec.vendas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entidade central do domínio — representa uma venda realizada.
 *
 * <p>Agrega todos os parâmetros que influenciam tanto o Pairwise Testing
 * quanto a análise de Complexidade Ciclomática:</p>
 *
 * <ul>
 *   <li>{@code cliente}       — tipo do cliente (COMUM / VIP / REVENDEDOR)</li>
 *   <li>{@code formaPagamento} — forma de pagamento (PIX / CARTÃO / BOLETO)</li>
 *   <li>{@code regiao}        — região de entrega (SUDESTE / NORDESTE / NORTE)</li>
 *   <li>{@code itens}         — lista de itens da venda</li>
 * </ul>
 *
 * <p>Os campos {@code valorBruto}, {@code desconto}, {@code frete} e
 * {@code valorFinal} são calculados pelo {@code VendaService}.</p>
 */
public class Venda {

    private final Long id;
    private final Cliente cliente;
    private final FormaPagamento formaPagamento;
    private final RegiaoEntrega regiao;
    private final LocalDateTime dataVenda;
    private final List<ItemVenda> itens;

    // ── Calculados pelo VendaService ──────────────────────────────────────
    private BigDecimal valorBruto  = BigDecimal.ZERO;
    private BigDecimal desconto    = BigDecimal.ZERO; // percentual 0.0 – 1.0
    private BigDecimal frete       = BigDecimal.ZERO;
    private BigDecimal valorFinal  = BigDecimal.ZERO;

    public Venda(Long id, Cliente cliente, FormaPagamento formaPagamento, RegiaoEntrega regiao) {
        if (id == null)             throw new IllegalArgumentException("ID da venda não pode ser nulo");
        if (cliente == null)        throw new IllegalArgumentException("Cliente não pode ser nulo");
        if (formaPagamento == null) throw new IllegalArgumentException("Forma de pagamento não pode ser nula");
        if (regiao == null)         throw new IllegalArgumentException("Região de entrega não pode ser nula");

        this.id             = id;
        this.cliente        = cliente;
        this.formaPagamento = formaPagamento;
        this.regiao         = regiao;
        this.dataVenda      = LocalDateTime.now();
        this.itens          = new ArrayList<>();
    }

    // ── Manipulação de itens ──────────────────────────────────────────────

    public void adicionarItem(ItemVenda item) {
        if (item == null) throw new IllegalArgumentException("Item não pode ser nulo");
        this.itens.add(item);
    }

    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(itens);
    }

    /**
     * Soma todos os subtotais dos itens (valor sem desconto e sem frete).
     */
    public BigDecimal calcularValorBruto() {
        return itens.stream()
                    .map(ItemVenda::calcularSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public Long getId()                      { return id;             }
    public Cliente getCliente()              { return cliente;        }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public RegiaoEntrega getRegiao()         { return regiao;         }
    public LocalDateTime getDataVenda()      { return dataVenda;      }

    public BigDecimal getValorBruto()  { return valorBruto;  }
    public BigDecimal getDesconto()    { return desconto;    }
    public BigDecimal getFrete()       { return frete;       }
    public BigDecimal getValorFinal()  { return valorFinal;  }

    public void setValorBruto(BigDecimal valorBruto)   { this.valorBruto  = valorBruto;  }
    public void setDesconto(BigDecimal desconto)        { this.desconto    = desconto;    }
    public void setFrete(BigDecimal frete)              { this.frete       = frete;       }
    public void setValorFinal(BigDecimal valorFinal)    { this.valorFinal  = valorFinal;  }

    @Override
    public String toString() {
        return "Venda{id=" + id
             + ", cliente=" + cliente.getNome()
             + ", pagamento=" + formaPagamento
             + ", regiao=" + regiao
             + ", bruto=" + valorBruto
             + ", desconto=" + desconto
             + ", frete=" + frete
             + ", final=" + valorFinal + "}";
    }
}
