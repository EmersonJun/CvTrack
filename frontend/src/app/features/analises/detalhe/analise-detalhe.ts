import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { Analise } from '../../../core/models/analise.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { mensagemDeErro } from '../../../core/utils/erros';
import { NotaIndicador } from '../../../shared/nota/nota-indicador';

@Component({
  selector: 'app-analise-detalhe',
  imports: [
    DatePipe,
    DecimalPipe,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    NotaIndicador,
  ],
  templateUrl: './analise-detalhe.html',
  styleUrl: './analise-detalhe.scss',
})
export class AnaliseDetalhe implements OnInit {
  private readonly analiseService = inject(AnaliseService);

  readonly id = input.required<string>();

  protected readonly analise = signal<Analise | null>(null);
  protected readonly erro = signal<string | null>(null);

  ngOnInit(): void {
    this.analiseService.buscar(this.id()).subscribe({
      next: (analise) => this.analise.set(analise),
      error: (erro) => this.erro.set(mensagemDeErro(erro, 'Análise não encontrada.')),
    });
  }
}
