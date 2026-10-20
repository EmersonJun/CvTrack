import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { filter, switchMap } from 'rxjs';
import { GrupoCurriculo } from '../../core/models/curriculo.models';
import { CurriculoService } from '../../core/services/curriculo.service';
import { NotificacaoService } from '../../core/services/notificacao.service';
import { aplicarErrosDoServidor, mensagemDeErro } from '../../core/utils/erros';
import { confirmar } from '../../shared/confirmacao/confirmacao';
import { TextoCurriculo } from './texto-curriculo';

export const TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;

export function validarArquivo(arquivo: File): string | null {
  const ehPdf = arquivo.type === 'application/pdf' || arquivo.name.toLowerCase().endsWith('.pdf');
  if (!ehPdf) {
    return 'Selecione um arquivo PDF.';
  }
  if (arquivo.size > TAMANHO_MAXIMO_BYTES) {
    return 'O arquivo deve ter no máximo 5 MB.';
  }
  return null;
}

@Component({
  selector: 'app-curriculos',
  imports: [
    DatePipe,
    DecimalPipe,
    ReactiveFormsModule,
    RouterLink,
    MatAutocompleteModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './curriculos.html',
  styleUrl: './curriculos.scss',
})
export class Curriculos implements OnInit {
  private readonly curriculoService = inject(CurriculoService);
  private readonly notificacao = inject(NotificacaoService);
  private readonly dialog = inject(MatDialog);

  protected readonly grupos = signal<GrupoCurriculo[]>([]);
  protected readonly carregando = signal(true);
  protected readonly enviando = signal(false);
  protected readonly arquivo = signal<File | null>(null);
  protected readonly erroArquivo = signal<string | null>(null);
  protected readonly titulos = computed(() => this.grupos().map((grupo) => grupo.titulo));

  protected readonly formulario = inject(NonNullableFormBuilder).group({
    titulo: ['', [Validators.required, Validators.maxLength(100)]],
  });

  ngOnInit(): void {
    this.carregar();
  }

  protected selecionarArquivo(evento: Event): void {
    const campo = evento.target as HTMLInputElement;
    const selecionado = campo.files?.item(0) ?? null;
    campo.value = '';

    if (!selecionado) {
      return;
    }
    const erro = validarArquivo(selecionado);
    this.erroArquivo.set(erro);
    this.arquivo.set(erro ? null : selecionado);
  }

  protected enviar(): void {
    const arquivo = this.arquivo();
    if (this.formulario.invalid || !arquivo) {
      this.formulario.markAllAsTouched();
      if (!arquivo) {
        this.erroArquivo.set('Selecione o arquivo PDF do currículo.');
      }
      return;
    }

    this.enviando.set(true);
    this.curriculoService.enviar(this.formulario.controls.titulo.value.trim(), arquivo).subscribe({
      next: (curriculo) => {
        this.notificacao.sucesso(`Versão ${curriculo.versao} de "${curriculo.titulo}" enviada.`);
        this.formulario.reset();
        this.arquivo.set(null);
        this.enviando.set(false);
        this.carregar();
      },
      error: (erro) => {
        this.enviando.set(false);
        if (!aplicarErrosDoServidor(this.formulario, erro)) {
          this.erroArquivo.set(mensagemDeErro(erro));
        }
      },
    });
  }

  protected verTexto(id: string): void {
    this.dialog.open(TextoCurriculo, { data: id, width: '760px', maxHeight: '85vh' });
  }

  protected excluir(id: string, titulo: string, versao: number): void {
    confirmar(this.dialog, {
      titulo: 'Excluir versão',
      mensagem: `A versão ${versao} de "${titulo}" e as análises dela serão excluídas. Deseja continuar?`,
      confirmar: 'Excluir',
    })
      .pipe(
        filter(Boolean),
        switchMap(() => this.curriculoService.excluir(id)),
      )
      .subscribe({
        next: () => {
          this.notificacao.sucesso('Versão excluída.');
          this.carregar();
        },
        error: (erro) => this.notificacao.erro(mensagemDeErro(erro)),
      });
  }

  private carregar(): void {
    this.curriculoService.listarAgrupados().subscribe({
      next: (grupos) => {
        this.grupos.set(grupos);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }
}
