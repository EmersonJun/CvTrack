import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { Comparacao } from '../../../core/models/analise.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { ComparacaoAnalises } from './comparacao';

describe('ComparacaoAnalises', () => {
  async function criar(resposta: Observable<Comparacao>): Promise<ComponentFixture<ComparacaoAnalises>> {
    await TestBed.configureTestingModule({
      imports: [ComparacaoAnalises],
      providers: [provideRouter([]), { provide: AnaliseService, useValue: { comparar: () => resposta } }],
    }).compileComponents();
    const fixture = TestBed.createComponent(ComparacaoAnalises);
    fixture.componentRef.setInput('anteriorId', 'a1');
    fixture.componentRef.setInput('atualId', 'a2');
    fixture.detectChanges();
    return fixture;
  }

  const resumo = (id: string, versao: number, nota: number) => ({
    id,
    nota,
    curriculo: { id: `c${versao}`, titulo: 'CV Backend', versao },
    vaga: { id: 'v1', titulo: 'Dev Java', empresa: null },
    criadoEm: '2026-10-01T10:00:00Z',
  });

  it('deve mostrar a variação da nota e as palavras conquistadas', async () => {
    const fixture = await criar(
      of({
        anterior: resumo('a1', 1, 50),
        atual: resumo('a2', 2, 80),
        variacaoNota: 30,
        palavrasConquistadas: ['Docker'],
        palavrasPerdidas: [],
        palavrasAindaAusentes: ['Kubernetes'],
      }),
    );
    const elemento: HTMLElement = fixture.nativeElement;

    expect(elemento.querySelector('.variacao')?.textContent).toContain('+30');
    expect(elemento.querySelector('.variacao')?.classList).toContain('subiu');
    expect(elemento.textContent).toContain('Docker');
    expect(elemento.textContent).toContain('Kubernetes');
  });

  it('deve mostrar o motivo quando as análises não são comparáveis', async () => {
    const fixture = await criar(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 422,
            error: { detail: 'Só é possível comparar versões do mesmo currículo analisadas para a mesma vaga' },
          }),
      ),
    );

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('mesma vaga');
  });
});
