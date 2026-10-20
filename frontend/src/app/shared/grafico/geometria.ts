export interface Dimensoes {
  largura: number;
  altura: number;
  margemEsquerda: number;
  margemDireita: number;
  margemTopo: number;
  margemBase: number;
}

export interface Coordenada {
  x: number;
  y: number;
}

export const DIMENSOES_PADRAO: Dimensoes = {
  largura: 640,
  altura: 260,
  margemEsquerda: 40,
  margemDireita: 24,
  margemTopo: 16,
  margemBase: 32,
};

export function posicaoX(indice: number, total: number, d: Dimensoes = DIMENSOES_PADRAO): number {
  const util = d.largura - d.margemEsquerda - d.margemDireita;
  if (total <= 1) {
    return d.margemEsquerda + util / 2;
  }
  return d.margemEsquerda + (util * indice) / (total - 1);
}

export function posicaoY(nota: number, d: Dimensoes = DIMENSOES_PADRAO): number {
  const util = d.altura - d.margemTopo - d.margemBase;
  const limitada = Math.max(0, Math.min(100, nota));
  return d.margemTopo + util * (1 - limitada / 100);
}

export function coordenadas(notas: number[], d: Dimensoes = DIMENSOES_PADRAO): Coordenada[] {
  return notas.map((nota, indice) => ({ x: posicaoX(indice, notas.length, d), y: posicaoY(nota, d) }));
}

export function caminhoDaLinha(pontos: Coordenada[]): string {
  return pontos.map((p, i) => `${i === 0 ? 'M' : 'L'}${p.x.toFixed(1)},${p.y.toFixed(1)}`).join(' ');
}
