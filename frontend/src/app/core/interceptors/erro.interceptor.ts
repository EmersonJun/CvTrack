import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { NotificacaoService } from '../services/notificacao.service';
import { SessaoService } from '../services/sessao.service';
import { mensagemDeErro } from '../utils/erros';

export const erroInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const sessao = inject(SessaoService);
  const router = inject(Router);
  const notificacao = inject(NotificacaoService);

  return proximo(requisicao).pipe(
    catchError((erro: unknown) => {
      if (erro instanceof HttpErrorResponse) {
        const rotaDeLogin = requisicao.url.includes('/auth/login') || requisicao.url.includes('/auth/registro');

        if (erro.status === 401 && !rotaDeLogin) {
          sessao.limpar();
          notificacao.erro('Sua sessão expirou. Faça login novamente.');
          router.navigate(['/login'], { queryParams: { retorno: router.url } });
        } else if (erro.status === 0 || erro.status >= 500) {
          notificacao.erro(mensagemDeErro(erro));
        }
      }
      return throwError(() => erro);
    }),
  );
};
