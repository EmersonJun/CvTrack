import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessaoService } from '../services/sessao.service';

export const authGuard: CanActivateFn = (_rota, estado) => {
  if (inject(SessaoService).ativa()) {
    return true;
  }
  return inject(Router).createUrlTree(['/login'], { queryParams: { retorno: estado.url } });
};

export const visitanteGuard: CanActivateFn = () => {
  if (!inject(SessaoService).ativa()) {
    return true;
  }
  return inject(Router).createUrlTree(['/dashboard']);
};
