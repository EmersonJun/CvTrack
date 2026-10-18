package br.com.cvmatch.auth;

import br.com.cvmatch.auth.dto.LoginRequest;
import br.com.cvmatch.auth.dto.RegistroRequest;
import br.com.cvmatch.auth.dto.TokenResponse;
import br.com.cvmatch.common.exception.ConflitoException;
import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.security.JwtService;
import br.com.cvmatch.security.UsuarioAutenticado;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.UsuarioRepository;
import br.com.cvmatch.usuario.dto.UsuarioResponse;
import br.com.cvmatch.usuario.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor // injeção por construtor: dependências final e fáceis de mockar
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;

    /** Cria a conta e já devolve um token (o usuário entra logado). */
    @Transactional
    public TokenResponse registrar(RegistroRequest requisicao) {
        String email = Usuario.normalizarEmail(requisicao.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Já existe uma conta com este e-mail");
        }

        Usuario usuario = usuarioRepository.save(new Usuario(
                requisicao.nome().trim(),
                email,
                passwordEncoder.encode(requisicao.senha())));

        return gerarToken(UsuarioAutenticado.de(usuario));
    }

    /**
     * Delega a verificação ao Spring Security: o AuthenticationManager busca o
     * usuário (UsuarioDetailsService) e compara a senha com o BCrypt.
     * Se falhar, lança BadCredentialsException -> 401 no GlobalExceptionHandler.
     */
    public TokenResponse login(LoginRequest requisicao) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        Usuario.normalizarEmail(requisicao.email()),
                        requisicao.senha()));

        return gerarToken((UsuarioAutenticado) autenticacao.getPrincipal());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPerfil(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(usuarioMapper::paraResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    private TokenResponse gerarToken(UsuarioAutenticado usuario) {
        return TokenResponse.bearer(jwtService.gerarToken(usuario), jwtService.getExpiracaoSegundos());
    }
}
