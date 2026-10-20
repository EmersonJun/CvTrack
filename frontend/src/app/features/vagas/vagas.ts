import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { filter, switchMap } from 'rxjs';
import { Vaga } from '../../core/models/vaga.models';
import { NotificacaoService } from '../../core/services/notificacao.service';
import { VagaService } from '../../core/services/vaga.service';
import { mensagemDeErro } from '../../core/utils/erros';
import { confirmar } from '../../shared/confirmacao/confirmacao';
import { VagaForm } from './vaga-form';

@Component({
  selector: 'app-vagas',
  imports: [DatePipe, RouterLink, MatButtonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule, MatTooltipModule],
  templateUrl: './vagas.html',
  styleUrl: './vagas.scss',
})
export class Vagas implements OnInit {
  private readonly vagaService = inject(VagaService);
  private readonly notificacao = inject(NotificacaoService);
  private readonly dialog = inject(MatDialog);

  protected readonly vagas = signal<Vaga[]>([]);
  protected readonly carregando = signal(true);

  ngOnInit(): void {
    this.carregar();
  }

  protected abrirFormulario(vaga: Vaga | null = null): void {
    this.dialog
      .open<VagaForm, Vaga | null, Vaga>(VagaForm, { data: vaga, width: '720px', maxHeight: '90vh' })
      .afterClosed()
      .pipe(filter((salva): salva is Vaga => !!salva))
      .subscribe(() => {
        this.notificacao.sucesso(vaga ? 'Vaga atualizada.' : 'Vaga cadastrada.');
        this.carregar();
      });
  }

  protected excluir(vaga: Vaga): void {
    confirmar(this.dialog, {
      titulo: 'Excluir vaga',
      mensagem: `A vaga "${vaga.titulo}" e as análises feitas para ela serão excluídas. Deseja continuar?`,
      confirmar: 'Excluir',
    })
      .pipe(
        filter(Boolean),
        switchMap(() => this.vagaService.excluir(vaga.id)),
      )
      .subscribe({
        next: () => {
          this.notificacao.sucesso('Vaga excluída.');
          this.carregar();
        },
        error: (erro) => this.notificacao.erro(mensagemDeErro(erro)),
      });
  }

  private carregar(): void {
    this.vagaService.listar().subscribe({
      next: (vagas) => {
        this.vagas.set(vagas);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }
}
