import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { Login } from './login';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let auth: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    auth = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    router = TestBed.inject(Router);
    spyOn(router, 'navigateByUrl');
    fixture.detectChanges();
  });

  function preencher(email: string, senha: string): void {
    const elemento: HTMLElement = fixture.nativeElement;
    const campos = elemento.querySelectorAll('input');
    campos[0].value = email;
    campos[0].dispatchEvent(new Event('input'));
    campos[1].value = senha;
    campos[1].dispatchEvent(new Event('input'));
  }

  function enviar(): void {
    (fixture.nativeElement as HTMLElement).querySelector('form')!.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  it('não deve chamar a API com o formulário inválido', () => {
    preencher('email-invalido', '');
    enviar();

    expect(auth.login).not.toHaveBeenCalled();
  });

  it('deve entrar e ir para o dashboard', () => {
    auth.login.and.returnValue(of({ id: '1', nome: 'Ana', email: 'ana@email.com', role: 'USER' }));

    preencher('ana@email.com', 'senha12345');
    enviar();

    expect(auth.login).toHaveBeenCalledWith({ email: 'ana@email.com', senha: 'senha12345' });
    expect(router.navigateByUrl).toHaveBeenCalledWith('/dashboard');
  });

  it('deve mostrar a mensagem de erro do servidor', () => {
    auth.login.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 401, error: { detail: 'E-mail ou senha inválidos' } })),
    );

    preencher('ana@email.com', 'errada');
    enviar();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('E-mail ou senha inválidos');
  });
});
