package br.com.cvmatch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Teste de fumaça: garante que o contexto do Spring sobe
 * (configuração, conexão com o banco e Flyway) sem erros.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CvMatchApplicationTests {

    @Test
    void contextLoads() {
    }
}
