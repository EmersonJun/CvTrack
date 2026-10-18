package br.com.cvmatch.common.exception;

/** Vira HTTP 404. Também usada quando o recurso existe mas é de outro usuário. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
