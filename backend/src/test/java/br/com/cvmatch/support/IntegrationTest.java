package br.com.cvmatch.support;

import br.com.cvmatch.TestcontainersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

/**
 * Base para testes de integração: sobe a aplicação inteira (segurança, controllers,
 * services, banco real no Testcontainers) e faz requisições HTTP simuladas com MockMvc.
 *
 * Todas as classes que herdam daqui compartilham o MESMO contexto Spring
 * (o Spring guarda em cache), então o container sobe uma vez só.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTest {

    @Autowired
    protected MockMvc mvc;

    /** E-mail único por teste: os testes não interferem uns nos outros. */
    protected String emailUnico() {
        return "teste-" + UUID.randomUUID() + "@email.com";
    }
}
