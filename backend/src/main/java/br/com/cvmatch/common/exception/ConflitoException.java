package br.com.cvmatch.common.exception;

/** Vira HTTP 409: o pedido conflita com o estado atual (ex.: e-mail já cadastrado). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
