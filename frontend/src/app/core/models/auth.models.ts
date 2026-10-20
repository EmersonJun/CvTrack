export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface TokenResponse {
  token: string;
  tipo: string;
  expiraEmSegundos: number;
}

export interface Usuario {
  id: string;
  nome: string;
  email: string;
  role: 'USER' | 'ADMIN';
}
