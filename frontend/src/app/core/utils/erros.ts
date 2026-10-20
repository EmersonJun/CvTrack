import { HttpErrorResponse } from '@angular/common/http';
import { FormGroup } from '@angular/forms';
import { ProblemDetail } from '../models/problem-detail';

const MENSAGEM_PADRAO = 'Ocorreu um erro inesperado. Tente novamente.';

export function problemDetail(erro: unknown): ProblemDetail | null {
  if (erro instanceof HttpErrorResponse && erro.error && typeof erro.error === 'object') {
    return erro.error as ProblemDetail;
  }
  return null;
}

export function mensagemDeErro(erro: unknown, padrao = MENSAGEM_PADRAO): string {
  if (erro instanceof HttpErrorResponse && erro.status === 0) {
    return 'Não foi possível conectar ao servidor.';
  }
  return problemDetail(erro)?.detail ?? padrao;
}

export function aplicarErrosDoServidor(formulario: FormGroup, erro: unknown): boolean {
  const campos = problemDetail(erro)?.campos ?? [];
  let aplicou = false;

  for (const { campo, mensagem } of campos) {
    const controle = formulario.get(campo);
    if (controle) {
      controle.setErrors({ ...controle.errors, servidor: mensagem });
      controle.markAsTouched();
      aplicou = true;
    }
  }
  return aplicou;
}
