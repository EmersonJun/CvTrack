import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorIntl, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterLink } from '@angular/router';
import { PaginadorPtBr } from '../../../core/i18n/paginador-pt-br';
import { AnaliseResumo, Pagina } from '../../../core/models/analise.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { NotaIndicador } from '../../../shared/nota/nota-indicador';

export const LIMITE_SELECAO = 2;

@Component({
  selector: 'app-historico',
  imports: [
    DatePipe,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatIconModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    NotaIndicador,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: PaginadorPtBr }],
  templateUrl: './historico.html',
  styleUrl: './historico.scss',
})
export class Historico implements OnInit {
  private readonly analiseService = inject(AnaliseService);
  private readonly router = inject(Router);

  protected readonly colunas = ['selecao', 'nota', 'curriculo', 'vaga', 'data', 'acoes'];
  protected readonly pagina = signal<Pagina<AnaliseResumo> | null>(null);
  protected readonly carregando = signal(true);
  protected readonly selecionadas = signal<AnaliseResumo[]>([]);
  protected readonly podeComparar = computed(() => this.selecionadas().length === LIMITE_SELECAO);

  ngOnInit(): void {
    this.carregar(0, 10);
  }

  protected mudarPagina(evento: PageEvent): void {
    this.carregar(evento.pageIndex, evento.pageSize);
  }

  protected selecionada(analise: AnaliseResumo): boolean {
    return this.selecionadas().some((item) => item.id === analise.id);
  }

  protected bloqueada(analise: AnaliseResumo): boolean {
    return !this.selecionada(analise) && this.selecionadas().length >= LIMITE_SELECAO;
  }

  protected alternar(analise: AnaliseResumo): void {
    this.selecionadas.update((atuais) =>
      atuais.some((item) => item.id === analise.id)
        ? atuais.filter((item) => item.id !== analise.id)
        : atuais.length < LIMITE_SELECAO
          ? [...atuais, analise]
          : atuais,
    );
  }

  protected comparar(): void {
    const [primeira, segunda] = this.selecionadas();
    this.router.navigate(['/analises/comparar'], {
      queryParams: { anteriorId: primeira.id, atualId: segunda.id },
    });
  }

  private carregar(pagina: number, tamanho: number): void {
    this.carregando.set(true);
    this.analiseService.listar(pagina, tamanho).subscribe({
      next: (resultado) => {
        this.pagina.set(resultado);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }
}
