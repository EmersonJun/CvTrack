import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { SessaoService } from '../services/sessao.service';
import { authGuard, visitanteGuard } from './auth.guard';

describe('guards de autenticação', () => {
  let sessao: SessaoService;
  const rota = {} as ActivatedRouteSnapshot;
  const estado = { url: '/vagas' } as RouterStateSnapshot;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [provideRouter([])] });
    sessao = TestBed.inject(SessaoService);
  });

  const executar = (guard: typeof authGuard) => TestBed.runInInjectionContext(() => guard(rota, estado));

  it('authGuard deve liberar quem está logado', () => {
    sessao.salvar({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    expect(executar(authGuard)).toBeTrue();
  });

  it('authGuard deve mandar visitante para o login guardando a rota de retorno', () => {
    const resultado = executar(authGuard) as UrlTree;

    expect(TestBed.inject(Router).serializeUrl(resultado)).toBe('/login?retorno=%2Fvagas');
  });

  it('visitanteGuard deve mandar quem já está logado para o dashboard', () => {
    sessao.salvar({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    const resultado = executar(visitanteGuard) as UrlTree;

    expect(TestBed.inject(Router).serializeUrl(resultado)).toBe('/dashboard');
  });
});
