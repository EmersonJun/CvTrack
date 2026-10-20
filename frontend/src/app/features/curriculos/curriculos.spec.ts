import { TAMANHO_MAXIMO_BYTES, validarArquivo } from './curriculos';

describe('validarArquivo', () => {
  it('deve aceitar PDF dentro do limite', () => {
    expect(validarArquivo(new File(['%PDF'], 'cv.pdf', { type: 'application/pdf' }))).toBeNull();
  });

  it('deve recusar arquivo que não é PDF', () => {
    expect(validarArquivo(new File(['texto'], 'cv.docx', { type: 'application/msword' }))).toContain('PDF');
  });

  it('deve recusar arquivo maior que 5 MB', () => {
    const grande = new File([new Uint8Array(TAMANHO_MAXIMO_BYTES + 1)], 'cv.pdf', { type: 'application/pdf' });

    expect(validarArquivo(grande)).toContain('5 MB');
  });
});
