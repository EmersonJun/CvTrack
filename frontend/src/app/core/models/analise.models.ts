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

export interface AnaliseResumo {
  id: string;
  nota: number;
  curriculo: CurriculoResumo;
  vaga: VagaResumo;
  criadoEm: string;
}

export interface Pagina<T> {
  conteudo: T[];
  pagina: number;
  tamanho: number;
  totalElementos: number;
  totalPaginas: number;
}

export interface Comparacao {
  anterior: AnaliseResumo;
  atual: AnaliseResumo;
  variacaoNota: number;
  palavrasConquistadas: string[];
  palavrasPerdidas: string[];
  palavrasAindaAusentes: string[];
}
