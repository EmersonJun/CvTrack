package br.com.cvmatch.support;

import br.com.cvmatch.TestcontainersConfiguration;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.vaga.entity.Vaga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

/**
 * Base para testes de repository.
 * - @DataJpaTest: sobe só a camada JPA (rápido) e faz rollback após cada teste.
 * - Replace.NONE: NÃO troca o banco por H2; usa o PostgreSQL do Testcontainers,
 *   então as migrations do Flyway e os tipos (jsonb, timestamptz) são testados de verdade.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public abstract class RepositoryTest {

    @Autowired
    protected TestEntityManager em;

    protected Usuario novoUsuario(String email) {
        return em.persist(new Usuario("Usuário Teste", email, "hash-bcrypt"));
    }

    protected Curriculo novoCurriculo(Usuario usuario, String titulo, int versao, String hash) {
        return em.persist(new Curriculo(usuario, titulo, versao, "cv.pdf", "Texto do currículo", hash));
    }

    protected Vaga novaVaga(Usuario usuario) {
        return em.persist(new Vaga(usuario, "Dev Java Júnior", "Empresa X", "Java, Spring Boot, SQL"));
    }
}
