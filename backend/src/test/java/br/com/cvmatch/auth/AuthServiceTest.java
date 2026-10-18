package br.com.cvmatch.auth;

import br.com.cvmatch.auth.dto.LoginRequest;
import br.com.cvmatch.auth.dto.RegistroRequest;
import br.com.cvmatch.common.exception.ConflitoException;
import br.com.cvmatch.security.JwtService;
import br.com.cvmatch.security.UsuarioAutenticado;
import br.com.cvmatch.usuario.Role;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.UsuarioRepository;
import br.com.cvmatch.usuario.mapper.UsuarioMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Teste unitário com Mockito: as dependências são "dublês" (mocks).
 * Testamos só a lógica do AuthService, sem banco e sem Spring, em milissegundos.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        // O mapper é real (código gerado pelo MapStruct), os demais são mocks
        UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);
        authService = new AuthService(usuarioRepository, passwordEncoder, authenticationManager, jwtService, mapper);
    }

    @Test
    void registrarDeveNormalizarEmailCriptografarSenhaEDevolverToken() {
        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senhaForte123")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(chamada -> chamada.getArgument(0));
        when(jwtService.gerarToken(any())).thenReturn("token-jwt");
        when(jwtService.getExpiracaoSegundos()).thenReturn(3600L);

        var resposta = authService.registrar(new RegistroRequest("  Ana  ", " ANA@Email.com ", "senhaForte123"));

        // ArgumentCaptor: "captura" o objeto passado ao save para inspecioná-lo
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();

        assertThat(salvo.getNome()).isEqualTo("Ana");
        assertThat(salvo.getEmail()).isEqualTo("ana@email.com");
        assertThat(salvo.getSenhaHash()).isEqualTo("hash-bcrypt"); // nunca a senha pura
        assertThat(resposta.token()).isEqualTo("token-jwt");
        assertThat(resposta.tipo()).isEqualTo("Bearer");
        assertThat(resposta.expiraEmSegundos()).isEqualTo(3600L);
    }

    @Test
    void registrarComEmailExistenteDeveLancarConflitoSemSalvar() {
        when(usuarioRepository.existsByEmail("ana@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(new RegistroRequest("Ana", "ana@email.com", "senhaForte123")))
                .isInstanceOf(ConflitoException.class);

        verify(usuarioRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void loginDeveAutenticarComEmailNormalizadoEDevolverToken() {
        var principal = new UsuarioAutenticado(UUID.randomUUID(), "ana@email.com", null, Role.USER);
        Authentication autenticado = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(autenticado);
        when(jwtService.gerarToken(principal)).thenReturn("token-jwt");

        var resposta = authService.login(new LoginRequest(" ANA@email.com", "senhaForte123"));

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("ana@email.com");
        assertThat(resposta.token()).isEqualTo("token-jwt");
    }

    @Test
    void loginComSenhaErradaDevePropagarBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("inválido"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("ana@email.com", "errada")))
                .isInstanceOf(BadCredentialsException.class);

        verifyNoInteractions(jwtService);
    }
}
