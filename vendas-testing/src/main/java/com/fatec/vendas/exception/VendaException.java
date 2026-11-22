package com.fatec.vendas.exception;

/**
 * Exceção de domínio para erros de negócio no sistema de vendas.
 *
 * <p>Lançada pelo {@code VendaService} quando uma regra de negócio é violada,
 * por exemplo: venda sem itens, estoque insuficiente, etc.</p>
 */
public class VendaException extends RuntimeException {

    public VendaException(String message) {
        super(message);
    }

    public VendaException(String message, Throwable cause) {
        super(message, cause);
    }
}
