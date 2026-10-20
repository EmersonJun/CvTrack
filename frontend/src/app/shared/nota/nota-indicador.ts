import { Component, computed, input } from '@angular/core';
import { descricaoDaNota, faixaDaNota } from './nota';

@Component({
  selector: 'app-nota-indicador',
  template: `
    <div
      class="anel"
      [class]="faixa()"
      [style.--progresso]="nota() + '%'"
      [style.width.px]="tamanho()"
      [style.height.px]="tamanho()"
      role="img"
      [attr.aria-label]="'Nota ' + nota() + ' de 100'"
    >
      <span class="valor" [style.font-size.px]="tamanho() * 0.28">{{ nota() }}</span>
    </div>
    @if (comDescricao()) {
      <span class="descricao">{{ descricao() }}</span>
    }
  `,
  styles: `
    :host {
      display: inline-flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
    }
    .anel {
      --cor: var(--mat-sys-error);
      display: grid;
      place-items: center;
      border-radius: 50%;
      background:
        radial-gradient(closest-side, var(--mat-sys-surface) 78%, transparent 80% 100%),
        conic-gradient(var(--cor) var(--progresso), var(--mat-sys-surface-variant) 0);
    }
    .anel.media { --cor: #c77700; }
    .anel.boa { --cor: #2e7d32; }
    .anel.excelente { --cor: #1b5e20; }
    .valor {
      font: var(--mat-sys-headline-medium);
      font-weight: 600;
    }
    .descricao {
      font: var(--mat-sys-label-large);
      color: var(--mat-sys-on-surface-variant);
    }
  `,
})
export class NotaIndicador {
  readonly nota = input.required<number>();
  readonly tamanho = input(120);
  readonly comDescricao = input(true);

  protected readonly faixa = computed(() => faixaDaNota(this.nota()));
  protected readonly descricao = computed(() => descricaoDaNota(this.nota()));
}
