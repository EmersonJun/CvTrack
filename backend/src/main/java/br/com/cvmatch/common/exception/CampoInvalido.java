package br.com.cvmatch.common.exception;

/** Um erro de validação de campo, devolvido na lista "campos" da resposta 400. */
public record CampoInvalido(String campo, String mensagem) {
}
