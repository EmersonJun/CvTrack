import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Observable } from 'rxjs';

export interface DadosConfirmacao {
  titulo: string;
  mensagem: string;
  confirmar?: string;
}

@Component({
  selector: 'app-confirmacao',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ dados.titulo }}</h2>
    <mat-dialog-content>{{ dados.mensagem }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancelar</button>
      <button mat-flat-button class="botao-perigo" [mat-dialog-close]="true">
        {{ dados.confirmar ?? 'Confirmar' }}
      </button>
    </mat-dialog-actions>
  `,
})
export class Confirmacao {
  protected readonly dados = inject<DadosConfirmacao>(MAT_DIALOG_DATA);
}

export function confirmar(dialog: MatDialog, dados: DadosConfirmacao): Observable<boolean | undefined> {
  return dialog.open(Confirmacao, { data: dados, width: '420px' }).afterClosed();
}
