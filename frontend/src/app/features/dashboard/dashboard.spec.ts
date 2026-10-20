import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { Painel } from '../../core/models/dashboard.models';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard } from './dashboard';

describe('Dashboard', () => {
  async function criar(painel: Painel): Promise<ComponentFixture<Dashboard>> {
    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [
        provideRouter([]),
        { provide: DashboardService, useValue: { carregar: () => of(painel) } },
        { provide: AuthService, useValue: { usuario: () => ({ nome: 'Ana' }) } },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(Dashboard);
    fixture.detectChanges();
    return fixture;
  }

  const vazio: Painel = {
    totalCurriculos: 0,
    totalVagas: 0,
    totalAnalises: 0,
    notaMedia: null,
    melhorNota: null,
    ultimaNota: null,
    evolucao: [],
    palavrasMaisAusentes: [],
  };

  it('sem análises deve mostrar o convite para começar', async () => {
    const fixture = await criar(vazio);
    const texto = (fixture.nativeElement as HTMLElement).textContent!;

    expect(texto).toContain('Olá, Ana!');
    expect(texto).toContain('Faça a primeira análise');
    expect((fixture.nativeElement as HTMLElement).querySelector('app-grafico-evolucao')).toBeNull();
  });

  it('com análises deve mostrar indicadores, gráfico e ranking', async () => {
    const fixture = await criar({
      ...vazio,
      totalAnalises: 2,
      notaMedia: 65,
      melhorNota: 80,
      ultimaNota: 80,
      evolucao: [
        { analiseId: 'a1', nota: 50, data: '2026-10-01', curriculoTitulo: 'CV', versao: 1, vagaTitulo: 'Dev' },
        { analiseId: 'a2', nota: 80, data: '2026-10-02', curriculoTitulo: 'CV', versao: 2, vagaTitulo: 'Dev' },
      ],
      palavrasMaisAusentes: [
        { termo: 'kubernetes', ocorrencias: 2 },
        { termo: 'docker', ocorrencias: 1 },
      ],
    });
    const elemento: HTMLElement = fixture.nativeElement;

    expect(elemento.querySelector('app-grafico-evolucao')).not.toBeNull();
    expect(elemento.querySelectorAll('.ranking li').length).toBe(2);
    expect((elemento.querySelectorAll('.ranking .barra')[1] as HTMLElement).style.width).toBe('50%');
  });
});
