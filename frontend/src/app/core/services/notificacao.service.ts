import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class NotificacaoService {
  private readonly snackBar = inject(MatSnackBar);

  sucesso(mensagem: string): void {
    this.snackBar.open(mensagem, 'OK', { duration: 3000, panelClass: 'notificacao-sucesso' });
  }

  erro(mensagem: string): void {
    this.snackBar.open(mensagem, 'Fechar', { duration: 6000, panelClass: 'notificacao-erro' });
  }
}
