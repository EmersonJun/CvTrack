import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { aplicarErrosDoServidor, mensagemDeErro } from '../../../core/utils/erros';

export const senhasIguais: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const senha = grupo.get('senha')?.value;
  const confirmacao = grupo.get('confirmacaoSenha')?.value;
  return senha && confirmacao && senha !== confirmacao ? { senhasDiferentes: true } : null;
};

@Component({
  selector: 'app-registro',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './registro.html',
})
export class Registro {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly enviando = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected readonly formulario = inject(NonNullableFormBuilder).group(
    {
      nome: ['', [Validators.required, Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
      senha: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
      confirmacaoSenha: ['', Validators.required],
    },
    { validators: senhasIguais },
  );

  protected criarConta(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.erro.set(null);
    const { nome, email, senha } = this.formulario.getRawValue();

    this.auth.registrar({ nome, email, senha }).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: (erro) => {
        this.enviando.set(false);
        if (erro instanceof HttpErrorResponse && erro.status === 409) {
          this.formulario.controls.email.setErrors({ servidor: mensagemDeErro(erro) });
          return;
        }
        if (!aplicarErrosDoServidor(this.formulario, erro)) {
          this.erro.set(mensagemDeErro(erro, 'Não foi possível criar a conta. Tente novamente.'));
        }
      },
    });
  }
}
