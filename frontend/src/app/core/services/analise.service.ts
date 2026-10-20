import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api';
import { Analise, NovaAnaliseRequest } from '../models/analise.models';

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
}
