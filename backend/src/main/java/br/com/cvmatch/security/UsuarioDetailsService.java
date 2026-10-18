package br.com.cvmatch.security;

import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Usado pelo Spring Security APENAS no login: busca o usuário pelo e-mail para
 * o DaoAuthenticationProvider comparar a senha com o hash BCrypt.
 *
 * Se o e-mail não existir, o Spring converte a UsernameNotFoundException em
 * BadCredentialsException, então a resposta é igual à de "senha errada".
 * Isso evita que alguém descubra quais e-mails estão cadastrados.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return usuarioRepository.findByEmail(Usuario.normalizarEmail(email))
                .map(UsuarioAutenticado::de)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
