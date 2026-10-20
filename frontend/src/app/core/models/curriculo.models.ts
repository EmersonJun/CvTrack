export interface Curriculo {
  id: string;
  titulo: string;
  versao: number;
  nomeArquivo: string;
  totalCaracteres: number;
  criadoEm: string;
}

export interface CurriculoDetalhe extends Curriculo {
  textoExtraido: string;
}

export interface GrupoCurriculo {
  titulo: string;
  versoes: Curriculo[];
}
