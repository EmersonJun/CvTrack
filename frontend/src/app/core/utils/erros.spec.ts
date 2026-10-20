import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup } from '@angular/forms';
import { aplicarErrosDoServidor, mensagemDeErro } from './erros';

describe('utilitários de erro', () => {
  it('deve usar o detail do Problem Details', () => {
    const erro = new HttpErrorResponse({ status: 409, error: { detail: 'E-mail já cadastrado' } });

    expect(mensagemDeErro(erro)).toBe('E-mail já cadastrado');
  });

  it('deve avisar quando o servidor está fora do ar', () => {
    expect(mensagemDeErro(new HttpErrorResponse({ status: 0 }))).toContain('conectar ao servidor');
  });

  it('deve marcar no formulário os campos rejeitados pelo servidor', () => {
    const formulario = new FormGroup({ nome: new FormControl(''), email: new FormControl('') });
    const erro = new HttpErrorResponse({
      status: 400,
      error: { campos: [{ campo: 'email', mensagem: 'E-mail inválido' }] },
    });

    const aplicou = aplicarErrosDoServidor(formulario, erro);

    expect(aplicou).toBeTrue();
    expect(formulario.controls.email.getError('servidor')).toBe('E-mail inválido');
    expect(formulario.controls.nome.errors).toBeNull();
  });
});
