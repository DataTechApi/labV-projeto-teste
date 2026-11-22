package com.fatec.vendas.testing.integracao;

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
 * Testes de integração — combinam Pairwise Testing + Complexidade Ciclomática
 * em fluxos completos do sistema de vendas sem mocks.
 *
 * <p>Estes testes exercitam o sistema de ponta a ponta (Model → Service → Service),
 * verificando que todos os componentes funcionam corretamente em conjunto.</p>
 *
 * <p>São selecionados a partir dos casos pairwise mais críticos, combinando
 * os cenários que exercitam mais ramos de decisão simultaneamente.</p>
 */
@Tag("integracao")
@DisplayName("Integração — Fluxo Completo de Vendas (Pairwise + CC)")
class VendaFluxoCompletoTest {

    private VendaService    vendaService;
    private DescontoService descontoService;
    private FreteService    freteService;

    @BeforeEach
    void setUp() {
        descontoService = new DescontoService();
        freteService    = new FreteService();
        vendaService    = new VendaService(descontoService, freteService);
    }

    // ══════════════════════════════════════════════════════════════════════
    // Cenário 1: Melhor caso possível (máximo desconto + frete grátis)
    // REVENDEDOR + PIX + SUDESTE + R$750 → teto 25% + frete R$0
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Integração 1: Melhor caso — REVENDEDOR + PIX + SUDESTE + R$750")
    void integracao_melhor_caso_maximo_desconto_frete_gratis() {
        // Arrange
        Produto notebook = new Produto(10L, "Notebook Pro", new BigDecimal("750.00"), 5);
        Cliente cliente  = new Cliente(10L, "Revenda Tech SP", TipoCliente.REVENDEDOR);

        Venda venda = new Venda(10L, cliente, FormaPagamento.PIX, RegiaoEntrega.SUDESTE);
        venda.adicionarItem(new ItemVenda(notebook, 1));

        // Act
        Venda resultado = vendaService.processarVenda(venda);

        // Assert
        // Desconto: 15% (REVENDEDOR) + 5% (PIX) + 8% (valor > 500) = 28% → teto 25%
        // Frete: SUDESTE, valorBruto > 300 → R$0
        // ValorFinal = 750 × 0.75 + 0 = R$562,50
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("750.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2500");
        assertThat(resultado.getFrete()).isEqualByComparingTo("0.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("562.50");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Cenário 2: Pior caso (sem desconto + frete máximo)
    // COMUM + BOLETO + NORTE + R$50 → 0% desc + R$45 frete
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Integração 2: Pior caso — COMUM + BOLETO + NORTE + R$50")
    void integracao_pior_caso_sem_desconto_frete_maximo() {
        Produto produto = new Produto(11L, "Caneta", new BigDecimal("50.00"), 100);
        Cliente cliente = new Cliente(11L, "João Ninguém", TipoCliente.COMUM);

        Venda venda = new Venda(11L, cliente, FormaPagamento.BOLETO, RegiaoEntrega.NORTE);
        venda.adicionarItem(new ItemVenda(produto, 1));

        Venda resultado = vendaService.processarVenda(venda);

        // Desconto BOLETO = -2% → zerado (proteção), valorBruto ≤ 500 → 0 total
        // Frete NORTE = R$45
        // ValorFinal = 50 × 1.00 + 45 = R$95,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("50.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.0000");
        assertThat(resultado.getFrete()).isEqualByComparingTo("45.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("95.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Cenário 3: Múltiplos itens na venda
    // VIP + PIX + NORDESTE + 3 itens diferentes
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Integração 3: Multiple itens — VIP + PIX + NORDESTE, 3 produtos")
    void integracao_multiplos_itens_venda() {
        Produto prod1 = new Produto(20L, "Teclado",  new BigDecimal("150.00"), 10);
        Produto prod2 = new Produto(21L, "Mouse",    new BigDecimal("80.00"),  10);
        Produto prod3 = new Produto(22L, "Headset",  new BigDecimal("220.00"), 10);

        Cliente cliente = new Cliente(20L, "Empresa VIP", TipoCliente.VIP);
        Venda venda = new Venda(20L, cliente, FormaPagamento.PIX, RegiaoEntrega.NORDESTE);

        venda.adicionarItem(new ItemVenda(prod1, 2)); // R$ 300,00
        venda.adicionarItem(new ItemVenda(prod2, 1)); // R$  80,00
        venda.adicionarItem(new ItemVenda(prod3, 1)); // R$ 220,00
        // Total bruto = R$ 600,00

        Venda resultado = vendaService.processarVenda(venda);

        // Desconto: VIP=10% + PIX=5% + ALTO(>500)=8% = 23%
        // Valor com desconto = 600 × 0.77 = R$462,00
        // Frete NORDESTE, valorBruto > 150 → R$20,00
        // ValorFinal = R$462,00 + R$20,00 = R$482,00
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("600.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.2300");
        assertThat(resultado.getFrete()).isEqualByComparingTo("20.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("482.00");
    }

    // ══════════════════════════════════════════════════════════════════════
    // Cenário 4: Verificação do valor bruto acumulado em venda com múltiplas unidades
    // ══════════════════════════════════════════════════════════════════════
    @Test
    @DisplayName("Integração 4: Quantidade × preço — REVENDEDOR + CARTÃO + NORDESTE + 5 unidades")
    void integracao_quantidade_preco_correto() {
        Produto produto = new Produto(30L, "Caderno", new BigDecimal("25.00"), 20);
        Cliente cliente = new Cliente(30L, "Papelaria ABC", TipoCliente.REVENDEDOR);

        Venda venda = new Venda(30L, cliente, FormaPagamento.CARTAO_CREDITO, RegiaoEntrega.NORDESTE);
        venda.adicionarItem(new ItemVenda(produto, 5)); // 5 × R$25 = R$125,00

        Venda resultado = vendaService.processarVenda(venda);

        // Desconto: REVENDEDOR=15%, valorBruto ≤ 500 → 15%
        // Valor com desconto = 125 × 0.85 = R$106,25
        // Frete NORDESTE, valorBruto=125 ≤ 150 → R$35,00
        // ValorFinal = R$106,25 + R$35,00 = R$141,25
        assertThat(resultado.getValorBruto()).isEqualByComparingTo("125.00");
        assertThat(resultado.getDesconto()).isEqualByComparingTo("0.1500");
        assertThat(resultado.getFrete()).isEqualByComparingTo("35.00");
        assertThat(resultado.getValorFinal()).isEqualByComparingTo("141.25");
    }
}
