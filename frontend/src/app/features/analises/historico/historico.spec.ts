import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AnaliseResumo } from '../../../core/models/analise.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { Historico } from './historico';

describe('Historico', () => {
  let fixture: ComponentFixture<Historico>;
  let router: Router;

  const analise = (id: string, versao: number): AnaliseResumo => ({
    id,
    nota: 50 + versao,
    curriculo: { id: `c${versao}`, titulo: 'CV Backend', versao },
    vaga: { id: 'v1', titulo: 'Dev Java', empresa: null },
    criadoEm: '2026-10-01T10:00:00Z',
  });

  beforeEach(async () => {
    const conteudo = [analise('a3', 3), analise('a2', 2), analise('a1', 1)];
    await TestBed.configureTestingModule({
      imports: [Historico],
      providers: [
        provideRouter([]),
        {
          provide: AnaliseService,
          useValue: { listar: () => of({ conteudo, pagina: 0, tamanho: 10, totalElementos: 3, totalPaginas: 1 }) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Historico);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  });

  const caixas = (): HTMLInputElement[] =>
    Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('mat-checkbox input'));

  it('deve listar as análises', () => {
    expect((fixture.nativeElement as HTMLElement).querySelectorAll('tr.mat-mdc-row').length).toBe(3);
  });

  it('deve permitir no máximo duas seleções e então comparar', () => {
    caixas()[0].click();
    caixas()[1].click();
    fixture.detectChanges();

    expect(caixas()[2].disabled).toBeTrue();

    const botaoComparar = (fixture.nativeElement as HTMLElement).querySelector('.acoes-cabecalho button') as HTMLButtonElement;
    expect(botaoComparar.disabled).toBeFalse();
    botaoComparar.click();

    expect(router.navigate).toHaveBeenCalledWith(['/analises/comparar'], {
      queryParams: { anteriorId: 'a3', atualId: 'a2' },
    });
  });
});
