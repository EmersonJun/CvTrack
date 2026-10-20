import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../api';
import { Vaga, VagaRequest } from '../models/vaga.models';

@Injectable({ providedIn: 'root' })
export class VagaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_URL}/vagas`;

  listar(): Observable<Vaga[]> {
    return this.http.get<Vaga[]>(this.url);
  }

  buscar(id: string): Observable<Vaga> {
    return this.http.get<Vaga>(`${this.url}/${id}`);
  }

  criar(vaga: VagaRequest): Observable<Vaga> {
    return this.http.post<Vaga>(this.url, vaga);
  }

  atualizar(id: string, vaga: VagaRequest): Observable<Vaga> {
    return this.http.put<Vaga>(`${this.url}/${id}`, vaga);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
