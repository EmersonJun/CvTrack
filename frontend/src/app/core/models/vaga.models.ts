export interface Vaga {
  id: string;
  titulo: string;
  empresa: string | null;
  descricao: string;
  criadoEm: string;
}

export interface VagaRequest {
  titulo: string;
  empresa: string | null;
  descricao: string;
}
