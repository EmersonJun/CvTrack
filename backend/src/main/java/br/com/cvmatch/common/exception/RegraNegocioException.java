package br.com.cvmatch.common.exception;

/**
 * Vira HTTP 422: os dados estão bem formados, mas violam uma regra de negócio
 * (ex.: PDF sem texto extraível).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
