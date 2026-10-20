import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_URL } from '../api';
import { SessaoService } from '../services/sessao.service';

export const authInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const token = inject(SessaoService).token();

  if (!token || !requisicao.url.startsWith(API_URL)) {
    return proximo(requisicao);
  }

  return proximo(
    requisicao.clone({ setHeaders: { Authorization: `Bearer ${token}` } }),
  );
};
