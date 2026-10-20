import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PontoEvolucao } from '../../core/models/dashboard.models';
import { GraficoEvolucao } from './grafico-evolucao';

describe('GraficoEvolucao', () => {
  let fixture: ComponentFixture<GraficoEvolucao>;

  const pontos: PontoEvolucao[] = [
    { analiseId: 'a1', nota: 50, data: '2026-10-01T10:00:00Z', curriculoTitulo: 'CV Backend', versao: 1, vagaTitulo: 'Dev Java' },
    { analiseId: 'a2', nota: 80, data: '2026-10-10T10:00:00Z', curriculoTitulo: 'CV Backend', versao: 2, vagaTitulo: 'Dev Java' },
  ];

  beforeEach(() => {
    fixture = TestBed.createComponent(GraficoEvolucao);
    fixture.componentRef.setInput('pontos', pontos);
    fixture.detectChanges();
  });

  const elemento = (): HTMLElement => fixture.nativeElement;

  it('deve desenhar um marcador por análise e rotular a última nota', () => {
    expect(elemento().querySelectorAll('circle.marcador').length).toBe(2);
    expect(elemento().querySelector('.rotulo-valor')?.textContent?.trim()).toBe('80');
  });

  it('deve descrever o gráfico para leitores de tela', () => {
    expect(elemento().querySelector('svg')?.getAttribute('aria-label')).toContain('de 50 para 80');
  });

  it('deve mostrar a dica ao passar o mouse sobre um ponto', () => {
    elemento().querySelectorAll('rect.alvo')[0].dispatchEvent(new Event('mouseenter'));
    fixture.detectChanges();

    const dica = elemento().querySelector('.dica');
    expect(dica?.textContent).toContain('Nota 50');
    expect(dica?.textContent).toContain('CV Backend · v1');
  });
});
