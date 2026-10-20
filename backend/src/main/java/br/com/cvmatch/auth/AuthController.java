package br.com.cvmatch.auth;

import br.com.cvmatch.auth.dto.LoginRequest;
import br.com.cvmatch.auth.dto.RegistroRequest;
import br.com.cvmatch.auth.dto.TokenResponse;
import br.com.cvmatch.security.UsuarioAutenticado;
import br.com.cvmatch.usuario.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Cadastro, login e perfil do usuário")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Cria uma conta e devolve o token JWT")
    @SecurityRequirements
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registrar(@Valid @RequestBody RegistroRequest requisicao) {
        return authService.registrar(requisicao);
    }

    @Operation(summary = "Autentica com e-mail e senha e devolve o token JWT")
    @SecurityRequirements
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest requisicao) {
        return authService.login(requisicao);
    }

    @Operation(summary = "Dados do usuário autenticado")
    @GetMapping("/me")
    public UsuarioResponse perfil(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return authService.buscarPerfil(usuario.id());
    }
}
