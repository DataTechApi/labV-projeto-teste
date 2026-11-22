package com.fatec.vendas.testing.pairwise;

import com.fatec.vendas.model.*;
import com.fatec.vendas.service.DescontoService;
import com.fatec.vendas.service.FreteService;
import com.fatec.vendas.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes derivados da técnica de PAIRWISE TESTING (All-Pairs).
 *
 * <p>Em vez de testar todas as 81 combinações possíveis de:
 * <ul>
 *   <li>3 tipos de cliente × 3 formas de pagamento × 3 regiões × 3 faixas de valor</li>
 * </ul>
 * utilizamos a matriz pairwise da classe {@link PairwiseMatrix} para reduzir
 * a 9 casos de teste que cobrem todos os PARES de valores.</p>
 *
 * <p>Economia: de 81 para 9 casos → <b>redução de ~89%</b> mantendo cobertura de pares.</p>
 *
 * @see PairwiseMatrix
 */
@Tag("pairwise")
@DisplayName("Pairwise Testing — Sistema de Vendas")
class PairwiseVendaTest {

    private VendaService    vendaService;
    private DescontoService descontoService;
    private FreteService    freteService;

    // ── Produtos base para os testes ──────────────────────────────────────
    private Produto produtoBarato;   // R$ 50,00 — faixa BAIXO
    private Produto produtoMedio;    // R$ 250,00 — faixa MEDIO
    private Produto produtoCaro;     // R$ 750,00 — faixa ALTO

    @BeforeEach
    void setUp() {
        descontoService = new DescontoService();
        freteService    = new FreteService();
        vendaService    = new VendaService(descontoService, freteService);

        produtoBarato = new Produto(1L, "Produto Barato",  new BigDecimal("50.00"),  100);
        produtoMedio  = new Produto(2L, "Produto Médio",   new BigDecimal("250.00"), 100);
        produtoCaro   = new Produto(3L, "Produto Caro",    new BigDecimal("750.00"), 100);
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC01 — COMUM | PIX | SUDESTE | BAIXO
    // Pares cobertos: COMUM-PIX, COMUM-SUDESTE, COMUM-BAIXO,
    //                 PIX-SUDESTE, PIX-BAIXO, SUDESTE-BAIXO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC01: COMUM + PIX + SUDESTE + R$50 → 5% desc + frete R$15")
    void tc01_comum_pix_sudeste_baixo() {
        // Arrange
        Cliente cliente = new Cliente(1L, "João Silva", TipoCliente.COMUM);
        Venda venda = new Venda(1L, cliente, FormaPagamento.PIX, RegiaoEntrega.SUDESTE);
        venda.adicionarItem(new ItemVenda(produtoBarato, 1)); // R$ 50,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Valor bruto = R$ 50,00
        // Desconto PIX = 5% → 50 × 0.05 = R$ 2,50 → valorComDesconto = R$ 47,50
        // Frete SUDESTE, valorBruto ≤ 300 → R$ 15,00
        // Valor final = R$ 47,50 + R$ 15,00 = R$ 62,50
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("50.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0500");
        assertThat(resultado.getFrete()).isEqualByComparingTo("15.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("62.50");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC02 — COMUM | CARTAO_CREDITO | NORDESTE | MEDIO
    // Pares cobertos: COMUM-CARTAO, COMUM-NORDESTE, COMUM-MEDIO,
    //                 CARTAO-NORDESTE, CARTAO-MEDIO, NORDESTE-MEDIO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC02: COMUM + CARTÃO + NORDESTE + R$250 → 0% desc + frete R$20")
    void tc02_comum_cartao_nordeste_medio() {
        // Arrange
        Cliente cliente = new Cliente(2L, "Maria Santos", TipoCliente.COMUM);
        Venda venda = new Venda(2L, cliente, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE);
        venda.adicionarItem(new ItemVenda(produtoMedio, 1)); // R$ 250,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto = 0% (COMUM + CARTAO sem bônus/taxa)
        // Frete NORDESTE, valorBruto > 150 → R$ 20,00
        // Valor final = R$ 250,00 + R$ 20,00 = R$ 270,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("250.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0000");
        assertThat(resultado.getFrete()).isEqualByComparingTo("20.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("270.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC03 — COMUM | BOLETO | NORTE | ALTO
    // Pares cobertos: COMUM-BOLETO, COMUM-NORTE, COMUM-ALTO,
    //                 BOLETO-NORTE, BOLETO-ALTO, NORTE-ALTO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC03: COMUM + BOLETO + NORTE + R$750 → 0% desc (boleto cancela) + frete R$45")
    void tc03_comum_boleto_norte_alto() {
        // Arrange
        Cliente cliente = new Cliente(3L, "Carlos Oliveira", TipoCliente.COMUM);
        Venda venda = new Venda(3L, cliente, FormaPagamento.BOLETO, RegiaoEntrega.NORTE);
        venda.adicionarItem(new ItemVenda(produtoCaro, 1)); // R$ 750,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto BOLETO = -2%, valorBruto > 500 = +8% → total = 6%
        // Valor com desconto = 750 × (1 - 0.06) = 750 × 0.94 = R$ 705,00
        // Frete NORTE = R$ 45,00
        // Valor final = R$ 705,00 + R$ 45,00 = R$ 750,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("750.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0600");
        assertThat(resultado.getFrete()).isEqualByComparingTo("45.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("750.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC04 — VIP | PIX | NORDESTE | ALTO
    // Pares cobertos: VIP-PIX, VIP-NORDESTE, VIP-ALTO,
    //                 PIX-NORDESTE, PIX-ALTO, NORDESTE-ALTO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC04: VIP + PIX + NORDESTE + R$750 → 23% desc + frete R$20")
    void tc04_vip_pix_nordeste_alto() {
        // Arrange
        Cliente cliente = new Cliente(4L, "Ana Costa", TipoCliente.VIP);
        Venda venda = new Venda(4L, cliente, FormaPagamento.PIX, RegiaoEntrega.NORDESTE);
        venda.adicionarItem(new ItemVenda(produtoCaro, 1)); // R$ 750,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto VIP=10% + PIX=5% + ALTO=8% = 23%
        // Valor com desconto = 750 × (1 - 0.23) = 750 × 0.77 = R$ 577,50
        // Frete NORDESTE, valorBruto > 150 → R$ 20,00
        // Valor final = R$ 577,50 + R$ 20,00 = R$ 597,50
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("750.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2300");
        assertThat(resultado.getFrete()).isEqualByComparingTo("20.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("597.50");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC05 — VIP | CARTAO_CREDITO | NORTE | BAIXO
    // Pares cobertos: VIP-CARTAO, VIP-NORTE, VIP-BAIXO,
    //                 CARTAO-NORTE, CARTAO-BAIXO, NORTE-BAIXO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC05: VIP + CARTÃO + NORTE + R$50 → 10% desc + frete R$45")
    void tc05_vip_cartao_norte_baixo() {
        // Arrange
        Cliente cliente = new Cliente(5L, "Pedro Lima", TipoCliente.VIP);
        Venda venda = new Venda(5L, cliente, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORTE);
        venda.adicionarItem(new ItemVenda(produtoBarato, 1)); // R$ 50,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto VIP=10%
        // Valor com desconto = 50 × 0.90 = R$ 45,00
        // Frete NORTE = R$ 45,00
        // Valor final = R$ 45,00 + R$ 45,00 = R$ 90,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("50.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.1000");
        assertThat(resultado.getFrete()).isEqualByComparingTo("45.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("90.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC06 — VIP | BOLETO | SUDESTE | MEDIO
    // Pares cobertos: VIP-BOLETO, VIP-SUDESTE, VIP-MEDIO,
    //                 BOLETO-SUDESTE, BOLETO-MEDIO, SUDESTE-MEDIO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC06: VIP + BOLETO + SUDESTE + R$250 → 8% desc + frete R$15")
    void tc06_vip_boleto_sudeste_medio() {
        // Arrange
        Cliente cliente = new Cliente(6L, "Lúcia Ferreira", TipoCliente.VIP);
        Venda venda = new Venda(6L, cliente, FormaPagamento.BOLETO, RegiaoEntrega.SUDESTE);
        venda.adicionarItem(new ItemVenda(produtoMedio, 1)); // R$ 250,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto VIP=10% + BOLETO=-2% = 8%
        // Valor com desconto = 250 × (1 - 0.08) = 250 × 0.92 = R$ 230,00
        // Frete SUDESTE, valorBruto ≤ 300 → R$ 15,00
        // Valor final = R$ 230,00 + R$ 15,00 = R$ 245,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("250.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0800");
        assertThat(resultado.getFrete()).isEqualByComparingTo("15.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("245.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC07 — REVENDEDOR | PIX | NORTE | MEDIO
    // Pares cobertos: REVENDEDOR-PIX, REVENDEDOR-NORTE, REVENDEDOR-MEDIO,
    //                 PIX-NORTE, PIX-MEDIO, NORTE-MEDIO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC07: REVENDEDOR + PIX + NORTE + R$250 → 20% desc + frete R$45")
    void tc07_revendedor_pix_norte_medio() {
        // Arrange
        Cliente cliente = new Cliente(7L, "Distribuidora ABC", TipoCliente.REVENDEDOR);
        Venda venda = new Venda(7L, cliente, FormaPagamento.PIX, RegiaoEntrega.NORTE);
        venda.adicionarItem(new ItemVenda(produtoMedio, 1)); // R$ 250,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto REVENDEDOR=15% + PIX=5% = 20%
        // Valor com desconto = 250 × (1 - 0.20) = 250 × 0.80 = R$ 200,00
        // Frete NORTE = R$ 45,00
        // Valor final = R$ 200,00 + R$ 45,00 = R$ 245,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("250.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2000");
        assertThat(resultado.getFrete()).isEqualByComparingTo("45.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("245.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC08 — REVENDEDOR | CARTAO_CREDITO | SUDESTE | ALTO
    // Pares cobertos: REVENDEDOR-CARTAO, REVENDEDOR-SUDESTE, REVENDEDOR-ALTO,
    //                 CARTAO-SUDESTE, CARTAO-ALTO, SUDESTE-ALTO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC08: REVENDEDOR + CARTÃO + SUDESTE + R$750 → 23% desc + frete R$0 (grátis)")
    void tc08_revendedor_cartao_sudeste_alto() {
        // Arrange
        Cliente cliente = new Cliente(8L, "Revenda XYZ", TipoCliente.REVENDEDOR);
        Venda venda = new Venda(8L, cliente, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.SUDESTE);
        venda.adicionarItem(new ItemVenda(produtoCaro, 1)); // R$ 750,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto REVENDEDOR=15% + ALTO=8% = 23%
        // Valor com desconto = 750 × (1 - 0.23) = 750 × 0.77 = R$ 577,50
        // Frete SUDESTE, valorBruto > 300 → R$ 0,00 (grátis)
        // Valor final = R$ 577,50 + R$ 0,00 = R$ 577,50
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("750.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2300");
        assertThat(resultado.getFrete()).isEqualByComparingTo("0.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("577.50");
    }

    // ══════════════════════════════════════════════════════════════════════
    // TC09 — REVENDEDOR | BOLETO | NORDESTE | BAIXO
    // Pares cobertos: REVENDEDOR-BOLETO, REVENDEDOR-NORDESTE, REVENDEDOR-BAIXO,
    //                 BOLETO-NORDESTE, BOLETO-BAIXO, NORDESTE-BAIXO
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("TC09: REVENDEDOR + BOLETO + NORDESTE + R$50 → 13% desc + frete R$35")
    void tc09_revendedor_boleto_nordeste_baixo() {
        // Arrange
        Cliente cliente = new Cliente(9L, "Revenda Sul", TipoCliente.REVENDEDOR);
        Venda venda = new Venda(9L, cliente, FormaPagamento.BOLETO, RegiaoEntrega.NORDESTE);
        venda.adicionarItem(new ItemVenda(produtoBarato, 1)); // R$ 50,00

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto REVENDEDOR=15% + BOLETO=-2% = 13%
        // Valor com desconto = 50 × (1 - 0.13) = 50 × 0.87 = R$ 43,50
        // Frete NORDESTE, valorBruto ≤ 150 → R$ 35,00
        // Valor final = R$ 43,50 + R$ 35,00 = R$ 78,50
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("50.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.1300");
        assertThat(resultado.getFrete()).isEqualByComparingTo("35.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("78.50");
    }
}
