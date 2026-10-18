package br.com.cvmatch.security;

import br.com.cvmatch.usuario.Role;
import br.com.cvmatch.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * O usuário logado, como o Spring Security o enxerga.
 * Fica separado da entidade Usuario para não misturar regras de segurança
 * com persistência. Nos controllers: @AuthenticationPrincipal UsuarioAutenticado.
 *
 * @param senhaHash só é preenchida no login (para o BCrypt comparar);
 *                  quando o usuário vem do token, é null.
 */
public record UsuarioAutenticado(UUID id, String email, String senhaHash, Role role) implements UserDetails {

    public static UsuarioAutenticado de(Usuario usuario) {
        return new UsuarioAutenticado(usuario.getId(), usuario.getEmail(), usuario.getSenhaHash(), usuario.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convenção do Spring: papéis começam com "ROLE_" (permite usar hasRole("ADMIN"))
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /** Sobrescrito para o hash da senha nunca aparecer em logs. */
    @Override
    public String toString() {
        return "UsuarioAutenticado[id=" + id + ", email=" + email + ", role=" + role + "]";
    }
}
