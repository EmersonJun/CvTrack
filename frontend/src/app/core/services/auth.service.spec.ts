import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { SessaoService } from './sessao.service';

describe('AuthService', () => {
  let auth: AuthService;
  let sessao: SessaoService;
  let http: HttpTestingController;

  const usuario = { id: '1', nome: 'Ana', email: 'ana@email.com', role: 'USER' as const };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    auth = TestBed.inject(AuthService);
    sessao = TestBed.inject(SessaoService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('login deve salvar o token e carregar o perfil', () => {
    let resultado: unknown;

    auth.login({ email: 'ana@email.com', senha: 'senha12345' }).subscribe((u) => (resultado = u));

    const login = http.expectOne('/api/auth/login');
    expect(login.request.method).toBe('POST');
    login.flush({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    http.expectOne('/api/auth/me').flush(usuario);

    expect(sessao.token()).toBe('jwt');
    expect(auth.usuario()).toEqual(usuario);
    expect(resultado).toEqual(usuario);
  });

  it('registro deve enviar os dados e iniciar a sessão', () => {
    auth.registrar({ nome: 'Ana', email: 'ana@email.com', senha: 'senha12345' }).subscribe();

    const registro = http.expectOne('/api/auth/registro');
    expect(registro.request.body.nome).toBe('Ana');
    registro.flush({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });
    http.expectOne('/api/auth/me').flush(usuario);

    expect(auth.autenticado()).toBeTrue();
  });

  it('logout deve limpar a sessão e voltar para o login', () => {
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    sessao.salvar({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    auth.logout();

    expect(auth.autenticado()).toBeFalse();
    expect(auth.usuario()).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
