import { Component, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [MatCardModule, MatIconModule],
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
      </mat-card>
    </section>
  `,
})
export class Dashboard {
  protected readonly auth = inject(AuthService);
}
