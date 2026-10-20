import { Injectable, signal } from '@angular/core';
import { TokenResponse } from '../models/auth.models';

interface Sessao {
  token: string;
  expiraEm: number;
}

const CHAVE = 'cvmatch.sessao';

@Injectable({ providedIn: 'root' })
export class SessaoService {
  private readonly sessao = signal<Sessao | null>(this.carregar());

  ativa(): boolean {
    const atual = this.sessao();
    return atual !== null && atual.expiraEm > Date.now();
  }

  token(): string | null {
    return this.ativa() ? this.sessao()!.token : null;
  }

  salvar(resposta: TokenResponse): void {
    const sessao: Sessao = {
      token: resposta.token,
      expiraEm: Date.now() + resposta.expiraEmSegundos * 1000,
    };
    localStorage.setItem(CHAVE, JSON.stringify(sessao));
    this.sessao.set(sessao);
  }

  limpar(): void {
    localStorage.removeItem(CHAVE);
    this.sessao.set(null);
  }

  private carregar(): Sessao | null {
    try {
      const salva = localStorage.getItem(CHAVE);
      return salva ? (JSON.parse(salva) as Sessao) : null;
    } catch {
      return null;
    }
  }
}
