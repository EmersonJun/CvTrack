export type FaixaNota = 'baixa' | 'media' | 'boa' | 'excelente';

export function faixaDaNota(nota: number): FaixaNota {
  if (nota >= 90) {
    return 'excelente';
  }
  if (nota >= 70) {
    return 'boa';
  }
  if (nota >= 40) {
    return 'media';
  }
  return 'baixa';
}

export function descricaoDaNota(nota: number): string {
  const descricoes: Record<FaixaNota, string> = {
    excelente: 'Excelente aderência',
    boa: 'Boa aderência',
    media: 'Aderência parcial',
    baixa: 'Baixa aderência',
  };
  return descricoes[faixaDaNota(nota)];
}
