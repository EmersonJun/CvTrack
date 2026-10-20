import { PaginadorPtBr } from './paginador-pt-br';

describe('PaginadorPtBr', () => {
  const paginador = new PaginadorPtBr();

  it('deve descrever o intervalo em português', () => {
    expect(paginador.getRangeLabel(1, 10, 25)).toBe('11 – 20 de 25');
    expect(paginador.getRangeLabel(2, 10, 25)).toBe('21 – 25 de 25');
    expect(paginador.getRangeLabel(0, 10, 0)).toBe('0 de 0');
  });
});
