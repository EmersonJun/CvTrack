import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AnaliseService } from './analise.service';

describe('AnaliseService', () => {
  let service: AnaliseService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(AnaliseService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('deve solicitar a análise com os ids do currículo e da vaga', () => {
    service.analisar({ curriculoId: 'c1', vagaId: 'v1' }).subscribe();

    const requisicao = http.expectOne('/api/analises');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual({ curriculoId: 'c1', vagaId: 'v1' });
  });

  it('deve buscar uma análise pelo id', () => {
    service.buscar('a1').subscribe();

    expect(http.expectOne('/api/analises/a1').request.method).toBe('GET');
  });
});

describe('AnaliseService - histórico e comparação', () => {
  let service: AnaliseService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(AnaliseService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('deve listar o histórico paginado', () => {
    service.listar(2, 10).subscribe();

    http.expectOne('/api/analises?pagina=2&tamanho=10').flush({});
  });

  it('deve comparar duas análises', () => {
    service.comparar('a1', 'a2').subscribe();

    http.expectOne('/api/analises/comparacao?anteriorId=a1&atualId=a2').flush({});
  });
});
