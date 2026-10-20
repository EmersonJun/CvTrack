import { Component, OnDestroy, OnInit, computed, inject, input, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { GrupoCurriculo } from '../../../core/models/curriculo.models';
import { Vaga } from '../../../core/models/vaga.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { CurriculoService } from '../../../core/services/curriculo.service';
import { VagaService } from '../../../core/services/vaga.service';
import { mensagemDeErro } from '../../../core/utils/erros';

export const DICAS = [
  'Lendo o seu currículo...',
  'Identificando os requisitos da vaga...',
  'Comparando palavras-chave...',
  'Calculando a nota de aderência...',
  'Escrevendo sugestões de melhoria...',
];

@Component({
  selector: 'app-nova-analise',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: './nova-analise.html',
  styleUrl: './nova-analise.scss',
})
export class NovaAnalise implements OnInit, OnDestroy {
  private readonly curriculoService = inject(CurriculoService);
  private readonly vagaService = inject(VagaService);
  private readonly analiseService = inject(AnaliseService);
  private readonly router = inject(Router);

  readonly curriculoId = input<string>();
  readonly vagaId = input<string>();

  protected readonly grupos = signal<GrupoCurriculo[]>([]);
  protected readonly vagas = signal<Vaga[]>([]);
  protected readonly carregando = signal(true);
  protected readonly analisando = signal(false);
  protected readonly erro = signal<string | null>(null);
  protected readonly indiceDica = signal(0);
  protected readonly dica = computed(() => DICAS[this.indiceDica() % DICAS.length]);
  protected readonly faltaCadastro = computed(
    () => !this.carregando() && (this.grupos().length === 0 || this.vagas().length === 0),
  );

  protected readonly formulario = inject(NonNullableFormBuilder).group({
    curriculoId: ['', Validators.required],
    vagaId: ['', Validators.required],
  });

  private temporizador?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    forkJoin({
      grupos: this.curriculoService.listarAgrupados(),
      vagas: this.vagaService.listar(),
    }).subscribe({
      next: ({ grupos, vagas }) => {
        this.grupos.set(grupos);
        this.vagas.set(vagas);
        this.formulario.patchValue({
          curriculoId: this.curriculoId() ?? grupos[0]?.versoes[0]?.id ?? '',
          vagaId: this.vagaId() ?? (vagas.length === 1 ? vagas[0].id : ''),
        });
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }

  ngOnDestroy(): void {
    this.pararDicas();
  }

  protected analisar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.analisando.set(true);
    this.erro.set(null);
    this.iniciarDicas();

    this.analiseService.analisar(this.formulario.getRawValue()).subscribe({
      next: (analise) => {
        this.pararDicas();
        this.router.navigate(['/analises', analise.id]);
      },
      error: (erro: HttpErrorResponse) => {
        this.pararDicas();
        this.analisando.set(false);
        this.erro.set(mensagemDeErro(erro, 'Não foi possível concluir a análise. Tente novamente.'));
      },
    });
  }

  private iniciarDicas(): void {
    this.indiceDica.set(0);
    this.temporizador = setInterval(() => this.indiceDica.update((indice) => indice + 1), 4000);
  }

  private pararDicas(): void {
    if (this.temporizador) {
      clearInterval(this.temporizador);
      this.temporizador = undefined;
    }
  }
}
