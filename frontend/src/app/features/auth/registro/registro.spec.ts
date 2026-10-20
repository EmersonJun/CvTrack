import { FormControl, FormGroup } from '@angular/forms';
import { senhasIguais } from './registro';

describe('validador senhasIguais', () => {
  it('deve acusar senhas diferentes', () => {
    const grupo = new FormGroup({
      senha: new FormControl('senha12345'),
      confirmacaoSenha: new FormControl('outra-senha'),
    });

    expect(senhasIguais(grupo)).toEqual({ senhasDiferentes: true });
  });

  it('deve aceitar senhas iguais', () => {
    const grupo = new FormGroup({
      senha: new FormControl('senha12345'),
      confirmacaoSenha: new FormControl('senha12345'),
    });

    expect(senhasIguais(grupo)).toBeNull();
  });
});
