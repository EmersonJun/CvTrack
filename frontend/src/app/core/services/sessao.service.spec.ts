import { TestBed } from '@angular/core/testing';
import { SessaoService } from './sessao.service';

describe('SessaoService', () => {
  beforeEach(() => localStorage.clear());

  function criar(): SessaoService {
    TestBed.resetTestingModule();
    return TestBed.inject(SessaoService);
  }

  it('deve salvar o token e considerar a sessão ativa', () => {
    const sessao = criar();

    sessao.salvar({ token: 'abc', tipo: 'Bearer', expiraEmSegundos: 3600 });

    expect(sessao.ativa()).toBeTrue();
    expect(sessao.token()).toBe('abc');
  });

  it('deve recuperar a sessão salva ao recarregar a página', () => {
    criar().salvar({ token: 'abc', tipo: 'Bearer', expiraEmSegundos: 3600 });

    expect(criar().token()).toBe('abc');
  });

  it('não deve devolver token expirado', () => {
    const sessao = criar();

    sessao.salvar({ token: 'abc', tipo: 'Bearer', expiraEmSegundos: -1 });

    expect(sessao.ativa()).toBeFalse();
    expect(sessao.token()).toBeNull();
  });

  it('deve limpar a sessão', () => {
    const sessao = criar();
    sessao.salvar({ token: 'abc', tipo: 'Bearer', expiraEmSegundos: 3600 });

    sessao.limpar();

    expect(sessao.token()).toBeNull();
    expect(localStorage.getItem('cvmatch.sessao')).toBeNull();
  });
});
