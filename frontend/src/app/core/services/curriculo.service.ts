import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { API_URL } from '../api';
import { Curriculo, CurriculoDetalhe, GrupoCurriculo } from '../models/curriculo.models';

@Injectable({ providedIn: 'root' })
export class CurriculoService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/curriculos`;

  listar(): Observable<Curriculo[]> {
    return this.http.get<Curriculo[]>(this.url);
  }

  listarAgrupados(): Observable<GrupoCurriculo[]> {
    return this.listar().pipe(map(agruparPorTitulo));
  }

  versoes(titulo: string): Observable<Curriculo[]> {
    return this.http.get<Curriculo[]>(`${this.url}/versoes`, { params: new HttpParams().set('titulo', titulo) });
  }

  buscar(id: string): Observable<CurriculoDetalhe> {
    return this.http.get<CurriculoDetalhe>(`${this.url}/${id}`);
  }

  enviar(titulo: string, arquivo: File): Observable<Curriculo> {
    const dados = new FormData();
    dados.append('titulo', titulo);
    dados.append('arquivo', arquivo, arquivo.name);
    return this.http.post<Curriculo>(this.url, dados);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

export function agruparPorTitulo(curriculos: Curriculo[]): GrupoCurriculo[] {
  const grupos = new Map<string, Curriculo[]>();
  for (const curriculo of curriculos) {
    grupos.set(curriculo.titulo, [...(grupos.get(curriculo.titulo) ?? []), curriculo]);
  }
  return [...grupos.entries()]
    .map(([titulo, versoes]) => ({ titulo, versoes: [...versoes].sort((a, b) => b.versao - a.versao) }))
    .sort((a, b) => b.versoes[0].criadoEm.localeCompare(a.versoes[0].criadoEm));
}
