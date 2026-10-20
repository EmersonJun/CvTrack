import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { Vaga } from '../../core/models/vaga.models';
import { VagaService } from '../../core/services/vaga.service';
import { VagaForm } from './vaga-form';

describe('VagaForm', () => {
  const descricao = 'Buscamos desenvolvedor Java com Spring Boot, PostgreSQL, Docker e testes.';
  let vagaService: jasmine.SpyObj<VagaService>;
  let dialogRef: jasmine.SpyObj<MatDialogRef<VagaForm, Vaga>>;

  async function criar(vaga: Vaga | null): Promise<ComponentFixture<VagaForm>> {
    vagaService = jasmine.createSpyObj<VagaService>('VagaService', ['criar', 'atualizar']);
    dialogRef = jasmine.createSpyObj<MatDialogRef<VagaForm, Vaga>>('MatDialogRef', ['close']);
    await TestBed.configureTestingModule({
      imports: [VagaForm],
      providers: [
        { provide: VagaService, useValue: vagaService },
        { provide: MatDialogRef, useValue: dialogRef },
        { provide: MAT_DIALOG_DATA, useValue: vaga },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(VagaForm);
    fixture.detectChanges();
    return fixture;
  }

  function preencher(fixture: ComponentFixture<VagaForm>, titulo: string, empresa: string, texto: string): void {
    const elemento: HTMLElement = fixture.nativeElement;
    const [campoTitulo, campoEmpresa] = Array.from(elemento.querySelectorAll('input'));
    const campoDescricao = elemento.querySelector('textarea')!;
    campoTitulo.value = titulo;
    campoTitulo.dispatchEvent(new Event('input'));
    campoEmpresa.value = empresa;
    campoEmpresa.dispatchEvent(new Event('input'));
    campoDescricao.value = texto;
    campoDescricao.dispatchEvent(new Event('input'));
  }

  function enviar(fixture: ComponentFixture<VagaForm>): void {
    (fixture.nativeElement as HTMLElement).querySelector('form')!.dispatchEvent(new Event('submit'));
  }

  it('não deve salvar descrição curta demais', async () => {
    const fixture = await criar(null);

    preencher(fixture, 'Dev Java', '', 'curta');
    enviar(fixture);

    expect(vagaService.criar).not.toHaveBeenCalled();
  });

  it('deve criar a vaga enviando empresa vazia como nula', async () => {
    const fixture = await criar(null);
    const salva = { id: '1', titulo: 'Dev Java', empresa: null, descricao, criadoEm: '' };
    vagaService.criar.and.returnValue(of(salva));

    preencher(fixture, '  Dev Java  ', '   ', descricao);
    enviar(fixture);

    expect(vagaService.criar).toHaveBeenCalledWith({ titulo: 'Dev Java', empresa: null, descricao });
    expect(dialogRef.close).toHaveBeenCalledWith(salva);
  });

  it('deve atualizar quando recebe uma vaga existente', async () => {
    const existente = { id: '7', titulo: 'Dev Java', empresa: 'Empresa X', descricao, criadoEm: '' };
    const fixture = await criar(existente);
    vagaService.atualizar.and.returnValue(of(existente));

    enviar(fixture);

    expect(vagaService.atualizar).toHaveBeenCalledWith('7', {
      titulo: 'Dev Java',
      empresa: 'Empresa X',
      descricao,
    });
  });
});
