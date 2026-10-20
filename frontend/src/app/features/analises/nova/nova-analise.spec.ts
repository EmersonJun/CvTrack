import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { Analise } from '../../../core/models/analise.models';
import { AnaliseService } from '../../../core/services/analise.service';
import { CurriculoService } from '../../../core/services/curriculo.service';
import { VagaService } from '../../../core/services/vaga.service';
import { NovaAnalise } from './nova-analise';

describe('NovaAnalise', () => {
  let fixture: ComponentFixture<NovaAnalise>;
  let analiseService: jasmine.SpyObj<AnaliseService>;
  let router: Router;

  const grupos = [
    {
      titulo: 'CV Backend',
      versoes: [
        { id: 'c2', titulo: 'CV Backend', versao: 2, nomeArquivo: 'cv.pdf', totalCaracteres: 10, criadoEm: '2026-10-02' },
        { id: 'c1', titulo: 'CV Backend', versao: 1, nomeArquivo: 'cv.pdf', totalCaracteres: 10, criadoEm: '2026-10-01' },
      ],
    },
  ];
  const vagas = [
    { id: 'v1', titulo: 'Dev Java', empresa: null, descricao: 'x', criadoEm: '' },
    { id: 'v2', titulo: 'Dev Angular', empresa: null, descricao: 'x', criadoEm: '' },
  ];

  async function criar(curriculoId?: string, vagaId?: string): Promise<void> {
    analiseService = jasmine.createSpyObj<AnaliseService>('AnaliseService', ['analisar']);
    await TestBed.configureTestingModule({
      imports: [NovaAnalise],
      providers: [
        provideRouter([]),
        { provide: AnaliseService, useValue: analiseService },
        { provide: CurriculoService, useValue: { listarAgrupados: () => of(grupos) } },
        { provide: VagaService, useValue: { listar: () => of(vagas) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NovaAnalise);
    if (curriculoId) {
      fixture.componentRef.setInput('curriculoId', curriculoId);
    }
    if (vagaId) {
      fixture.componentRef.setInput('vagaId', vagaId);
    }
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    fixture.detectChanges();
  }

  function enviar(): void {
    (fixture.nativeElement as HTMLElement).querySelector('form')!.dispatchEvent(new Event('submit'));
  }

  it('deve pré-selecionar o currículo e a vaga vindos da URL e analisar', async () => {
    await criar('c1', 'v2');
    analiseService.analisar.and.returnValue(of({ id: 'a1' } as Analise));

    enviar();

    expect(analiseService.analisar).toHaveBeenCalledWith({ curriculoId: 'c1', vagaId: 'v2' });
    expect(router.navigate).toHaveBeenCalledWith(['/analises', 'a1']);
  });

  it('sem parâmetros deve sugerir a versão mais recente e exigir a escolha da vaga', async () => {
    await criar();

    enviar();

    expect(analiseService.analisar).not.toHaveBeenCalled();
    expect(fixture.componentInstance['formulario'].value.curriculoId).toBe('c2');
  });
});
