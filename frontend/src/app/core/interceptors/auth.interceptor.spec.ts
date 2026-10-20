import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SessaoService } from '../services/sessao.service';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let controlador: HttpTestingController;
  let sessao: SessaoService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([authInterceptor])), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpClient);
    controlador = TestBed.inject(HttpTestingController);
    sessao = TestBed.inject(SessaoService);
  });

  afterEach(() => controlador.verify());

  it('deve enviar o token nas chamadas para a API', () => {
    sessao.salvar({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    http.get('/api/vagas').subscribe();

    const requisicao = controlador.expectOne('/api/vagas');
    expect(requisicao.request.headers.get('Authorization')).toBe('Bearer jwt');
    requisicao.flush([]);
  });

  it('não deve enviar cabeçalho quando não há sessão', () => {
    http.get('/api/vagas').subscribe();

    const requisicao = controlador.expectOne('/api/vagas');
    expect(requisicao.request.headers.has('Authorization')).toBeFalse();
    requisicao.flush([]);
  });

  it('não deve vazar o token para outros domínios', () => {
    sessao.salvar({ token: 'jwt', tipo: 'Bearer', expiraEmSegundos: 3600 });

    http.get('https://exemplo.com/dados').subscribe();

    const requisicao = controlador.expectOne('https://exemplo.com/dados');
    expect(requisicao.request.headers.has('Authorization')).toBeFalse();
    requisicao.flush({});
  });
});
