import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Observable } from 'rxjs';
import { Vaga, VagaRequest } from '../../core/models/vaga.models';
import { VagaService } from '../../core/services/vaga.service';
import { aplicarErrosDoServidor, mensagemDeErro } from '../../core/utils/erros';

@Component({
  selector: 'app-vaga-form',
  imports: [ReactiveFormsModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatIconModule],
  templateUrl: './vaga-form.html',
})
export class VagaForm {
  private readonly vagaService = inject(VagaService);
  private readonly dialogRef = inject(MatDialogRef<VagaForm, Vaga>);
  protected readonly vaga = inject<Vaga | null>(MAT_DIALOG_DATA, { optional: true });

  protected readonly salvando = signal(false);
  protected readonly erro = signal<string | null>(null);

  protected readonly formulario = inject(NonNullableFormBuilder).group({
    titulo: [this.vaga?.titulo ?? '', [Validators.required, Validators.maxLength(150)]],
    empresa: [this.vaga?.empresa ?? '', Validators.maxLength(150)],
    descricao: [
      this.vaga?.descricao ?? '',
      [Validators.required, Validators.minLength(50), Validators.maxLength(20000)],
    ],
  });

  protected salvar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const valores = this.formulario.getRawValue();
    const requisicao: VagaRequest = {
      titulo: valores.titulo.trim(),
      empresa: valores.empresa.trim() || null,
      descricao: valores.descricao.trim(),
    };
    const operacao: Observable<Vaga> = this.vaga
      ? this.vagaService.atualizar(this.vaga.id, requisicao)
      : this.vagaService.criar(requisicao);

    this.salvando.set(true);
    this.erro.set(null);
    operacao.subscribe({
      next: (vaga) => this.dialogRef.close(vaga),
      error: (erro) => {
        this.salvando.set(false);
        if (!aplicarErrosDoServidor(this.formulario, erro)) {
          this.erro.set(mensagemDeErro(erro));
        }
      },
    });
  }
}
