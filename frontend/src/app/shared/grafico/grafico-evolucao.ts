import { DatePipe } from '@angular/common';
import { Component, DestroyRef, ElementRef, afterNextRender, computed, inject, input, signal } from '@angular/core';
import { PontoEvolucao } from '../../core/models/dashboard.models';
import { DIMENSOES_PADRAO, Dimensoes, caminhoDaLinha, coordenadas, posicaoY } from './geometria';

@Component({
  selector: 'app-grafico-evolucao',
  imports: [DatePipe],
  templateUrl: './grafico-evolucao.html',
  styleUrl: './grafico-evolucao.scss',
})
export class GraficoEvolucao {
  readonly pontos = input.required<PontoEvolucao[]>();

  private readonly largura = signal(DIMENSOES_PADRAO.largura);
  protected readonly selecionado = signal<number | null>(null);

  protected readonly d = computed<Dimensoes>(() => ({ ...DIMENSOES_PADRAO, largura: this.largura() }));
  protected readonly linhasGrade = computed(() =>
    [0, 25, 50, 75, 100].map((valor) => ({ valor, y: posicaoY(valor, this.d()) })),
  );
  protected readonly coordenadas = computed(() => coordenadas(this.pontos().map((p) => p.nota), this.d()));
  protected readonly caminho = computed(() => caminhoDaLinha(this.coordenadas()));
  protected readonly larguraFaixa = computed(() => {
    const total = this.pontos().length;
    const util = this.d().largura - this.d().margemEsquerda - this.d().margemDireita;
    return total <= 1 ? util : util / (total - 1);
  });
  protected readonly rotulosX = computed(() => {
    const total = this.pontos().length;
    const indices = total <= 2 ? [...Array(total).keys()] : [0, Math.floor((total - 1) / 2), total - 1];
    return indices.map((i) => ({ x: this.coordenadas()[i].x, data: this.pontos()[i].data }));
  });
  protected readonly descricao = computed(() => {
    const pontos = this.pontos();
    if (pontos.length === 0) {
      return 'Nenhuma análise ainda';
    }
    return `Evolução da nota em ${pontos.length} análises, de ${pontos[0].nota} para ${pontos[pontos.length - 1].nota}`;
  });
  protected readonly ultimo = computed(() => {
    const lista = this.coordenadas();
    return lista.length ? { ...lista[lista.length - 1], nota: this.pontos()[lista.length - 1].nota } : null;
  });
  protected readonly dica = computed(() => {
    const indice = this.selecionado();
    if (indice === null) {
      return null;
    }
    const coordenada = this.coordenadas()[indice];
    return {
      ponto: this.pontos()[indice],
      esquerda: coordenada.x,
      topo: coordenada.y,
      alinharDireita: coordenada.x > this.d().largura * 0.6,
    };
  });

  constructor() {
    const elemento = inject(ElementRef<HTMLElement>).nativeElement as HTMLElement;
    const destroyRef = inject(DestroyRef);

    afterNextRender(() => {
      const observador = new ResizeObserver(([entrada]) => {
        const largura = Math.round(entrada.contentRect.width);
        if (largura > 0) {
          this.largura.set(largura);
        }
      });
      observador.observe(elemento);
      destroyRef.onDestroy(() => observador.disconnect());
    });
  }

  protected selecionar(indice: number | null): void {
    this.selecionado.set(indice);
  }
}
