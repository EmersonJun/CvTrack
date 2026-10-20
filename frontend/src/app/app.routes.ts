import { Routes } from '@angular/router';
import { authGuard, visitanteGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [visitanteGuard],
    title: 'Entrar | CV Match',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    canActivate: [visitanteGuard],
    title: 'Criar conta | CV Match',
    loadComponent: () => import('./features/auth/registro/registro').then((m) => m.Registro),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/shell/shell').then((m) => m.Shell),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        title: 'Dashboard | CV Match',
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'curriculos',
        title: 'Currículos | CV Match',
        loadComponent: () => import('./features/curriculos/curriculos').then((m) => m.Curriculos),
      },
      {
        path: 'vagas',
        title: 'Vagas | CV Match',
        loadComponent: () => import('./features/vagas/vagas').then((m) => m.Vagas),
      },
      {
        path: 'analises/nova',
        title: 'Nova análise | CV Match',
        loadComponent: () =>
          import('./features/analises/nova/nova-analise').then((m) => m.NovaAnalise),
      },
      {
        path: 'analises/:id',
        title: 'Resultado da análise | CV Match',
        loadComponent: () =>
          import('./features/analises/detalhe/analise-detalhe').then((m) => m.AnaliseDetalhe),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
