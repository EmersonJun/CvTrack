package br.com.cvmatch.analise;

import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.PalavraChave;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.analise.repository.AnaliseRepository;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.support.RepositoryTest;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.vaga.entity.Vaga;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class AnaliseRepositoryTest extends RepositoryTest {

    @Autowired
    private AnaliseRepository repository;

    private Usuario usuario;
    private Curriculo curriculo;
    private Vaga vaga;

    @BeforeEach
    void setUp() {
        usuario = novoUsuario("ana@email.com");
        curriculo = novoCurriculo(usuario, "CV Backend", 1, "hash-1");
        vaga = novaVaga(usuario);
    }

    private Analise novaAnalise(int nota) {
        return new Analise(curriculo, vaga, nota, "Boa aderência",
                List.of("Experiência com Spring"), List.of("Citar Docker"),
                "ollama", "llama3.1", 1200L);
    }

    @Test
    void deveSalvarAnaliseComListasJsonEPalavrasChaveEmCascata() {
        var analise = novaAnalise(78);
        analise.adicionarPalavraChave("Java", TipoPalavraChave.PRESENTE);
        analise.adicionarPalavraChave("Docker", TipoPalavraChave.AUSENTE);
        repository.saveAndFlush(analise);
        em.clear();

        var salva = repository.findByIdAndCurriculoUsuarioId(analise.getId(), usuario.getId()).orElseThrow();

        assertThat(salva.getNota()).isEqualTo(78);
        assertThat(salva.getPontosFortes()).containsExactly("Experiência com Spring");
        assertThat(salva.getSugestoes()).containsExactly("Citar Docker");
        assertThat(salva.getPalavrasChave())
                .extracting(PalavraChave::getTermo, PalavraChave::getTipo)
                .containsExactlyInAnyOrder(
                        tuple("Java", TipoPalavraChave.PRESENTE),
                        tuple("Docker", TipoPalavraChave.AUSENTE));
    }

    @Test
    void naoDeveRetornarAnaliseDeOutroUsuario() {
        var analise = repository.saveAndFlush(novaAnalise(50));
        var outro = novoUsuario("outro@email.com");

        assertThat(repository.findByIdAndCurriculoUsuarioId(analise.getId(), outro.getId())).isEmpty();
    }

    @Test
    void deveRejeitarNotaForaDoIntervalo() {
        assertThatThrownBy(() -> repository.saveAndFlush(novaAnalise(150)))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void deveListarHistoricoPaginado() {
        repository.save(novaAnalise(40));
        repository.save(novaAnalise(60));
        repository.save(novaAnalise(80));
        repository.flush();

        var pagina = repository.findAllByCurriculoUsuarioId(usuario.getId(), PageRequest.of(0, 2));

        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getContent()).hasSize(2);
    }
}
