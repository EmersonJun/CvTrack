import { descricaoDaNota, faixaDaNota } from './nota';

describe('faixas de nota', () => {
  it('deve classificar cada faixa nos limites corretos', () => {
    expect(faixaDaNota(0)).toBe('baixa');
    expect(faixaDaNota(39)).toBe('baixa');
    expect(faixaDaNota(40)).toBe('media');
    expect(faixaDaNota(69)).toBe('media');
    expect(faixaDaNota(70)).toBe('boa');
    expect(faixaDaNota(89)).toBe('boa');
    expect(faixaDaNota(90)).toBe('excelente');
    expect(faixaDaNota(100)).toBe('excelente');
  });

  it('deve descrever a nota em texto', () => {
    expect(descricaoDaNota(75)).toBe('Boa aderência');
  });
});
