package com.fatec.vendas.tradicional;

import com.fatec.vendas.model.*;
import com.fatec.vendas.service.DescontoService;
import com.fatec.vendas.service.FreteService;
import com.fatec.vendas.service.VendaService;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

/**
 * Classe base com utilitários compartilhados por todos os testes tradicionais.
 *
 * <p><b>CONTEXTO — ABORDAGEM TRADICIONAL (SEM TÉCNICAS)</b></p>
 *
 * <p>Neste projeto, os testes são escritos da forma que a maioria dos
 * desenvolvedores faz sem treinamento específico em técnicas de teste:
 * <ul>
 *   <li>Testa cada parâmetro <b>isoladamente</b>, sem considerar combinações</li>
 *   <li>Repete casos já cobertos sem perceber (redundância)</li>
 *   <li>Não tem método para saber quando "é suficiente"</li>
 *   <li>Perde caminhos críticos de execução</li>
 *   <li>Sem rastreabilidade do que foi ou não coberto</li>
 * </ul>
 *
 * <p><b>Comparativo com o projeto vendas-testing:</b></p>
 * <pre>
 * ┌─────────────────────────────┬──────────────────┬─────────────────────┐
 * │ Aspecto                     │ Tradicional      │ Com Técnicas        │
 * ├─────────────────────────────┼──────────────────┼─────────────────────┤
 * │ Total de casos              │ 55+ casos        │ 33 casos            │
 * │ Redundância                 │ ALTA (~40%)      │ ZERO                │
 * │ Cobertura de pares          │ Aleatória        │ 100% (54/54 pares)  │
 * │ Cobertura de ramos          │ ~65% estimada    │ 100%                │
 * │ Rastreabilidade             │ Nenhuma          │ Total               │
 * │ Manutenção                  │ Difícil          │ Fácil               │
 * └─────────────────────────────┴──────────────────┴─────────────────────┘
 * </pre>
 */
public abstract class BaseTesteTradicional {

    protected VendaService    vendaService;
    protected DescontoService descontoService;
    protected FreteService    freteService;

    // Produtos fixos usados nos testes
    protected Produto produtoA;  // R$ 50,00
    protected Produto produtoB;  // R$ 200,00
    protected Produto produtoC;  // R$ 600,00

    @BeforeEach
    void setUpBase() {
        descontoService = new DescontoService();
        freteService    = new FreteService();
        vendaService    = new VendaService(descontoService, freteService);

        produtoA = new Produto(1L, "Produto A", new BigDecimal("50.00"),  100);
        produtoB = new Produto(2L, "Produto B", new BigDecimal("200.00"), 100);
        produtoC = new Produto(3L, "Produto C", new BigDecimal("600.00"), 100);
    }

    /** Monta uma venda pronta com 1 item. */
    protected Venda criarVenda(long id, TipoCliente tipoCliente,
                               FormaPagamento pagamento,
                               RegiaoEntrega regiao,
                               Produto produto) {
        Cliente cliente = new Cliente(id, "Cliente " + id, tipoCliente);
        Venda venda = new Venda(id, cliente, pagamento, regiao);
        venda.adicionarItem(new ItemVenda(produto, 1));
        return venda;
    }
}
