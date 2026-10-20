import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CurriculoDetalhe } from '../../core/models/curriculo.models';
import { CurriculoService } from '../../core/services/curriculo.service';

@Component({
  selector: 'app-texto-curriculo',
  imports: [MatDialogModule, MatButtonModule, MatProgressSpinnerModule],
  template: `
    @if (curriculo(); as dados) {
      <h2 mat-dialog-title>{{ dados.titulo }} · v{{ dados.versao }}</h2>
      <mat-dialog-content>
        <p class="texto-secundario">Este é o texto que a IA lê ao analisar o currículo.</p>
        <pre class="texto">{{ dados.textoExtraido }}</pre>
      </mat-dialog-content>
    } @else {
      <mat-dialog-content class="carregando-pagina"><mat-spinner diameter="40" /></mat-dialog-content>
    }
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Fechar</button>
    </mat-dialog-actions>
  `,
  styles: `
    .texto {
      white-space: pre-wrap;
      font: var(--mat-sys-body-medium);
      background: var(--mat-sys-surface-container);
      padding: 16px;
      border-radius: 12px;
      margin: 0;
    }
  `,
})
export class TextoCurriculo implements OnInit {
  private readonly id = inject<string>(MAT_DIALOG_DATA);
  private readonly curriculoService = inject(CurriculoService);

  protected readonly curriculo = signal<CurriculoDetalhe | null>(null);

  ngOnInit(): void {
    this.curriculoService.buscar(this.id).subscribe((curriculo) => this.curriculo.set(curriculo));
  }
}
