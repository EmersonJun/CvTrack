export interface CurriculoResumo {
  id: string;
  titulo: string;
  versao: number;
}

export interface VagaResumo {
  id: string;
  titulo: string;
  empresa: string | null;
}

export interface Analise {
  id: string;
  nota: number;
  resumo: string | null;
  pontosFortes: string[];
  sugestoes: string[];
  palavrasPresentes: string[];
  palavrasAusentes: string[];
  curriculo: CurriculoResumo;
  vaga: VagaResumo;
  provedorIa: string;
  modelo: string;
  tempoProcessamentoMs: number | null;
  criadoEm: string;
}

export interface NovaAnaliseRequest {
  curriculoId: string;
  vagaId: string;
}
