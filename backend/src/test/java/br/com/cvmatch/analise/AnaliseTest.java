package br.com.cvmatch.analise;

import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.vaga.entity.Vaga;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Teste unitário puro (sem Spring, sem banco): regras da própria entidade. */
class AnaliseTest {

    private final Usuario ana = new Usuario("Ana", "ana@email.com", "hash");
    private final Usuario bruno = new Usuario("Bruno", "bruno@email.com", "hash");

    @Test
    void naoDevePermitirCurriculoEVagaDeUsuariosDiferentes() {
        var curriculo = new Curriculo(ana, "CV", 1, "cv.pdf", "texto", "hash");
        var vagaDoBruno = new Vaga(bruno, "Dev", null, "desc");

        assertThatThrownBy(() -> new Analise(curriculo, vagaDoBruno, 70, null,
                List.of(), List.of(), "ollama", "llama3.1", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveVincularPalavraChaveAAnaliseEProtegerAsListas() {
        var curriculo = new Curriculo(ana, "CV", 1, "cv.pdf", "texto", "hash");
        var vaga = new Vaga(ana, "Dev", null, "desc");
        var pontosFortes = new ArrayList<>(List.of("Java"));
        var analise = new Analise(curriculo, vaga, 70, null, pontosFortes, List.of(), "ollama", "llama3.1", null);

        analise.adicionarPalavraChave("Spring", TipoPalavraChave.PRESENTE);
        pontosFortes.add("alterado por fora"); // não deve afetar a entidade (cópia defensiva)

        assertThat(analise.getPalavrasChave().getFirst().getAnalise()).isSameAs(analise);
        assertThat(analise.getPontosFortes()).containsExactly("Java");
        assertThatThrownBy(() -> analise.getSugestoes().add("x"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void curriculoNaoDeveAceitarVersaoZero() {
        assertThatThrownBy(() -> new Curriculo(ana, "CV", 0, "cv.pdf", "texto", "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
