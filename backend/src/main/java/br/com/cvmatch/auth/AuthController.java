package br.com.cvmatch.auth;

import br.com.cvmatch.auth.dto.LoginRequest;
import br.com.cvmatch.auth.dto.RegistroRequest;
import br.com.cvmatch.auth.dto.TokenResponse;
import br.com.cvmatch.security.UsuarioAutenticado;
import br.com.cvmatch.usuario.dto.UsuarioResponse;
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

/**
 * Controller "magro": só recebe a requisição, valida (@Valid) e delega ao service.
 * Nenhuma regra de negócio mora aqui.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registrar(@Valid @RequestBody RegistroRequest requisicao) {
        return authService.registrar(requisicao);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest requisicao) {
        return authService.login(requisicao);
    }

    /** Rota protegida: o Spring injeta o usuário que o JwtAuthFilter colocou no contexto. */
    @GetMapping("/me")
    public UsuarioResponse perfil(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return authService.buscarPerfil(usuario.id());
    }
}
