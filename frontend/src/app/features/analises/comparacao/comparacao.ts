import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, input, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { Comparacao } from '../../../core/models/analise.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { mensagemDeErro } from '../../../core/utils/erros';
import { NotaIndicador } from '../../../shared/nota/nota-indicador';

@Component({
  selector: 'app-comparacao',
  imports: [DatePipe, RouterLink, MatButtonModule, MatCardModule, MatChipsModule, MatIconModule, MatProgressSpinnerModule, NotaIndicador],
  templateUrl: './comparacao.html',
  styleUrl: './comparacao.scss',
})
export class ComparacaoAnalises implements OnInit {
  private readonly analiseService = inject(AnaliseService);

  readonly anteriorId = input.required<string>();
  readonly atualId = input.required<string>();

  protected readonly comparacao = signal<Comparacao | null>(null);
  protected readonly erro = signal<string | null>(null);

  ngOnInit(): void {
    this.analiseService.comparar(this.anteriorId(), this.atualId()).subscribe({
      next: (resultado) => this.comparacao.set(resultado),
      error: (erro) => this.erro.set(mensagemDeErro(erro, 'Não foi possível comparar as análises.')),
    });
  }
}
