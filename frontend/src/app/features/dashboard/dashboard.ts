import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { Painel } from '../../core/models/dashboard.models';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { GraficoEvolucao } from '../../shared/grafico/grafico-evolucao';

@Component({
  selector: 'app-dashboard',
  imports: [DecimalPipe, RouterLink, MatButtonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule, GraficoEvolucao],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly dashboardService = inject(DashboardService);

  protected readonly painel = signal<Painel | null>(null);
  protected readonly carregando = signal(true);
  protected readonly maiorOcorrencia = computed(() =>
    Math.max(1, ...(this.painel()?.palavrasMaisAusentes.map((termo) => termo.ocorrencias) ?? [])),
  );

  ngOnInit(): void {
    this.dashboardService.carregar().subscribe({
      next: (painel) => {
        this.painel.set(painel);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false),
    });
  }
}
