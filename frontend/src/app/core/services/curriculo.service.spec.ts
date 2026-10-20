import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Curriculo } from '../models/curriculo.models';
import { CurriculoService, agruparPorTitulo } from './curriculo.service';

function curriculo(titulo: string, versao: number, criadoEm: string): Curriculo {
  return { id: `${titulo}-${versao}`, titulo, versao, nomeArquivo: 'cv.pdf', totalCaracteres: 100, criadoEm };
}

describe('CurriculoService', () => {
  let service: CurriculoService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(CurriculoService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('deve enviar o PDF como multipart com título e arquivo', () => {
    const arquivo = new File(['%PDF-1.7'], 'cv.pdf', { type: 'application/pdf' });

    service.enviar('CV Backend', arquivo).subscribe();

    const requisicao = http.expectOne('/api/curriculos');
    const corpo = requisicao.request.body as FormData;
    expect(requisicao.request.method).toBe('POST');
    expect(corpo.get('titulo')).toBe('CV Backend');
    expect((corpo.get('arquivo') as File).name).toBe('cv.pdf');
    requisicao.flush(curriculo('CV Backend', 1, '2026-10-20T10:00:00Z'));
  });

  it('deve buscar as versões de um título', () => {
    service.versoes('CV Backend').subscribe();

    http.expectOne('/api/curriculos/versoes?titulo=CV%20Backend').flush([]);
  });

  it('deve agrupar por título com a versão mais nova primeiro', () => {
    const grupos = agruparPorTitulo([
      curriculo('CV Backend', 1, '2026-10-01T10:00:00Z'),
      curriculo('CV Front', 1, '2026-10-05T10:00:00Z'),
      curriculo('CV Backend', 2, '2026-10-10T10:00:00Z'),
    ]);

    expect(grupos.map((grupo) => grupo.titulo)).toEqual(['CV Backend', 'CV Front']);
    expect(grupos[0].versoes.map((versao) => versao.versao)).toEqual([2, 1]);
  });
});
