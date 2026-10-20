import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { VagaService } from './vaga.service';

describe('VagaService', () => {
  let service: VagaService;
  let http: HttpTestingController;
  const vaga = { titulo: 'Dev Java', empresa: null, descricao: 'Descrição' };

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(VagaService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('deve criar com POST', () => {
    service.criar(vaga).subscribe();

    const requisicao = http.expectOne('/api/vagas');
    expect(requisicao.request.method).toBe('POST');
    expect(requisicao.request.body).toEqual(vaga);
    requisicao.flush({});
  });

  it('deve atualizar com PUT no id', () => {
    service.atualizar('42', vaga).subscribe();

    expect(http.expectOne('/api/vagas/42').request.method).toBe('PUT');
  });

  it('deve excluir com DELETE no id', () => {
    service.excluir('42').subscribe();

    expect(http.expectOne('/api/vagas/42').request.method).toBe('DELETE');
  });
});
