export interface PontoEvolucao {
  analiseId: string;
  nota: number;
  data: string;
  curriculoTitulo: string;
  versao: number;
  vagaTitulo: string;
}

export interface TermoFrequente {
  termo: string;
  ocorrencias: number;
}

export interface Painel {
  totalCurriculos: number;
  totalVagas: number;
  totalAnalises: number;
  notaMedia: number | null;
  melhorNota: number | null;
  ultimaNota: number | null;
  evolucao: PontoEvolucao[];
  palavrasMaisAusentes: TermoFrequente[];
}
