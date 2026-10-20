package br.com.cvmatch.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Roda uma vez por requisição: lê "Authorization: Bearer <token>", valida o token
 * e, se ele for válido, coloca o usuário no SecurityContext.
 *
 * NÃO é um @Component de propósito: o Spring Boot registraria qualquer Filter
 * que fosse bean também na cadeia de servlets, e ele rodaria duas vezes.
 * Ele é criado manualmente dentro do SecurityConfig.
 */
@RequiredArgsConstructor
public class ;JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIXO)) {
            String token = header.substring(PREFIXO.length());

            jwtService.validar(token).ifPresent(usuario -> {
                var autenticacao = new UsernamePasswordAuthenticationToken(
                        usuario, null, usuario.getAuthorities());
                autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            });
        }

        // Sem token (ou com token inválido) a requisição segue anônima.
        // Se a rota exigir login, o Spring Security responde 401 mais adiante.
        chain.doFilter(request, response);
    }
}
