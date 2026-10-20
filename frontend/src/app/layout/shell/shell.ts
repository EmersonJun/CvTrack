import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

interface ItemMenu {
  rota: string;
  rotulo: string;
  icone: string;
}

@Component({
  selector: 'app-shell',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
  ],
  templateUrl: './shell.html',
  styleUrl: './shell.scss',
})
export class Shell implements OnInit {
  protected readonly auth = inject(AuthService);

  protected readonly menu: ItemMenu[] = [
    { rota: '/dashboard', rotulo: 'Dashboard', icone: 'insights' },
    { rota: '/curriculos', rotulo: 'Currículos', icone: 'description' },
    { rota: '/vagas', rotulo: 'Vagas', icone: 'work_outline' },
    { rota: '/analises/nova', rotulo: 'Nova análise', icone: 'auto_awesome' },
  ];

  ngOnInit(): void {
    if (!this.auth.usuario()) {
      this.auth.carregarPerfil().subscribe({ error: () => undefined });
    }
  }

  protected sair(): void {
    this.auth.logout();
  }
}
