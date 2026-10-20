import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { NotificacaoService } from '../services/notificacao.service';
import { SessaoService } from '../services/sessao.service';
import { erroInterceptor } from './erro.interceptor';

describe('erroInterceptor', () => {
  let http: HttpClient;
  let controlador: HttpTestingController;
  let sessao: SessaoService;
  let router: Router;
  let notificacao: jasmine.SpyObj<NotificacaoService>;

  beforeEach(() => {
    localStorage.clear();
    notificacao = jasmine.createSpyObj<NotificacaoService>('NotificacaoService', ['erro', 'sucesso']);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([erroInterceptor])),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: NotificacaoService, useValue: notificacao },
      ],
    });
    http = TestBed.inject(HttpClient);
    controlador = TestBed.inject(HttpTestingController);
    sessao = TestBed.inject(SessaoService);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
  });

  afterEach(() => controlador.verify());

  it('401 deve encerrar a sessão e mandar para o login', () => {
    sessao.salvar({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    http.get('/api/vagas').subscribe({ error: () => undefined });
    controlador.expectOne('/api/vagas').flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(sessao.ativa()).toBeFalse();
    expect(router.navigate).toHaveBeenCalled();
    expect(notificacao.erro).toHaveBeenCalled();
  });

  it('401 no login não deve redirecionar (é só senha errada)', () => {
    http.post('/api/auth/login', {}).subscribe({ error: () => undefined });
    controlador.expectOne('/api/auth/login').flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('erro 500 deve exibir a mensagem do servidor', () => {
    http.get('/api/dashboard').subscribe({ error: () => undefined });
    controlador
      .expectOne('/api/dashboard')
      .flush({ detail: 'Erro interno' }, { status: 500, statusText: 'Server Error' });

    expect(notificacao.erro).toHaveBeenCalledWith('Erro interno');
  });

  it('erro 502 da IA deve ser tratado pela própria tela', () => {
    http.post('/api/analises', {}).subscribe({ error: () => undefined });
    controlador.expectOne('/api/analises').flush({}, { status: 502, statusText: 'Bad Gateway' });

    expect(notificacao.erro).not.toHaveBeenCalled();
  });

  it('erro 400 deve ser tratado pela própria tela', () => {
    http.post('/api/vagas', {}).subscribe({ error: () => undefined });
    controlador.expectOne('/api/vagas').flush({}, { status: 400, statusText: 'Bad Request' });

    expect(notificacao.erro).not.toHaveBeenCalled();
  });
});
