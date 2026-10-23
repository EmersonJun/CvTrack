package br.com.cvmatch.security;

import br.com.cvmatch.usuario.Role;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-mais-de-32-caracteres";

    private final JwtService jwtService = new JwtService(new JwtProperties(SEGREDO, 60));
    private final UsuarioAutenticado usuario =
            new UsuarioAutenticado(UUID.randomUUID(), "ana@email.com", null, Role.USER);

    @Test
    void deveGerarTokenEValidarDevolvendoOMesmoUsuario() {
        String token = jwtService.gerarToken(usuario);

        assertThat(jwtService.validar(token)).hasValue(usuario);
    }

    @Test
    void deveRejeitarTokenAssinadoComOutraChave() {
        var outroServico = new JwtService(new JwtProperties("outro-segredo-totalmente-diferente-1234567", 60));
        String tokenFalsificado = outroServico.gerarToken(usuario);

        assertThat(jwtService.validar(tokenFalsificado)).isEmpty();
    }

    @Test
    void deveRejeitarTokenExpirado() {
        var servicoQueGeraTokensVencidos = new JwtService(new JwtProperties(SEGREDO, -1));
        String tokenVencido = servicoQueGeraTokensVencidos.gerarToken(usuario);

        assertThat(jwtService.validar(tokenVencido)).isEmpty();
    }

    @Test
    void deveRejeitarTextoQueNaoEToken() {
        assertThat(jwtService.validar("isso-nao-e-um-jwt")).isEmpty();
    }

    @Test
    void naoDeveSubirComSegredoCurto() {
        assertThatThrownBy(() -> new JwtService(new JwtProperties("curto", 60)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deveInformarExpiracaoEmSegundos() {
        assertThat(jwtService.getExpiracaoSegundos()).isEqualTo(3600);
    }
}
