import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, MatButtonModule, MatCardModule, MatIconModule],
  template: `
    <section class="pagina">
      <div class="pagina-cabecalho">
        <h1>Olá{{ auth.usuario() ? ', ' + auth.usuario()!.nome : '' }}!</h1>
      </div>
      <mat-card appearance="outlined">
        <mat-card-content>
          <p>
            Envie um currículo em PDF, cadastre uma vaga e descubra a nota de aderência entre eles.
          </p>
        </mat-card-content>
        <mat-card-actions>
          <a mat-button routerLink="/curriculos">Enviar currículo</a>
          <a mat-button routerLink="/vagas">Cadastrar vaga</a>
          <a mat-flat-button routerLink="/analises/nova">Nova análise</a>
        </mat-card-actions>
      </mat-card>
    </section>
  `,
})
export class Dashboard {
  protected readonly auth = inject(AuthService);
}
