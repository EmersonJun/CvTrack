import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api';
import { Analise, AnaliseResumo, Comparacao, NovaAnaliseRequest, Pagina } from '../models/analise.models';

@Injectable({ providedIn: 'root' })
export class AnaliseService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/analises`;

  analisar(requisicao: NovaAnaliseRequest): Observable<Analise> {
    return this.http.post<Analise>(this.url, requisicao);
  }

  buscar(id: string): Observable<Analise> {
    return this.http.get<Analise>(`${this.url}/${id}`);
  }

  listar(pagina: number, tamanho: number): Observable<Pagina<AnaliseResumo>> {
    const params = new HttpParams().set('pagina', pagina).set('tamanho', tamanho);
    return this.http.get<Pagina<AnaliseResumo>>(this.url, { params });
  }

  comparar(anteriorId: string, atualId: string): Observable<Comparacao> {
    const params = new HttpParams().set('anteriorId', anteriorId).set('atualId', atualId);
    return this.http.get<Comparacao>(`${this.url}/comparacao`, { params });
  }
}
