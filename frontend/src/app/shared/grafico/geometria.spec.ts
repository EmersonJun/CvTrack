import { DIMENSOES_PADRAO, caminhoDaLinha, coordenadas, posicaoX, posicaoY } from './geometria';

describe('geometria do gráfico', () => {
  const d = DIMENSOES_PADRAO;

  it('nota 100 fica no topo e nota 0 na base da área útil', () => {
    expect(posicaoY(100)).toBe(d.margemTopo);
    expect(posicaoY(0)).toBe(d.altura - d.margemBase);
  });

  it('deve limitar notas fora do intervalo', () => {
    expect(posicaoY(150)).toBe(posicaoY(100));
    expect(posicaoY(-10)).toBe(posicaoY(0));
  });

  it('deve distribuir os pontos de ponta a ponta', () => {
    expect(posicaoX(0, 3)).toBe(d.margemEsquerda);
    expect(posicaoX(2, 3)).toBe(d.largura - d.margemDireita);
  });

  it('um único ponto fica centralizado', () => {
    expect(posicaoX(0, 1)).toBe(d.margemEsquerda + (d.largura - d.margemEsquerda - d.margemDireita) / 2);
  });

  it('deve montar o caminho SVG da linha', () => {
    const caminho = caminhoDaLinha(coordenadas([0, 100]));

    expect(caminho.startsWith('M')).toBeTrue();
    expect(caminho).toContain(' L');
  });
});
