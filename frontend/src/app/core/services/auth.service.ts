import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, switchMap, tap } from 'rxjs';
import { API_URL } from '../api';
import { LoginRequest, RegistroRequest, TokenResponse, Usuario } from '../models/auth.models';
import { SessaoService } from './sessao.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly sessao = inject(SessaoService);
  private readonly router = inject(Router);

  private readonly usuarioAtual = signal<Usuario | null>(null);
  readonly usuario = this.usuarioAtual.asReadonly();

  autenticado(): boolean {
    return this.sessao.ativa();
  }

  login(dados: LoginRequest): Observable<Usuario> {
    return this.iniciarSessao(this.http.post<TokenResponse>(`${API_URL}/auth/login`, dados));
  }

  registrar(dados: RegistroRequest): Observable<Usuario> {
    return this.iniciarSessao(this.http.post<TokenResponse>(`${API_URL}/auth/registro`, dados));
  }

  carregarPerfil(): Observable<Usuario> {
    return this.http
      .get<Usuario>(`${API_URL}/auth/me`)
      .pipe(tap((usuario) => this.usuarioAtual.set(usuario)));
  }

  logout(): void {
    this.sessao.limpar();
    this.usuarioAtual.set(null);
    this.router.navigate(['/login']);
  }

  private iniciarSessao(requisicao: Observable<TokenResponse>): Observable<Usuario> {
    return requisicao.pipe(
      tap((resposta) => this.sessao.salvar(resposta)),
      switchMap(() => this.carregarPerfil()),
    );
  }
}
