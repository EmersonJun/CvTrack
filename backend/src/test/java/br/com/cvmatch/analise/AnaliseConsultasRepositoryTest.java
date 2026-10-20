package br.com.cvmatch.analise;

import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.analise.repository.AnaliseRepository;
import br.com.cvmatch.analise.repository.PontoEvolucaoView;
import br.com.cvmatch.analise.repository.TermoFrequenteView;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.support.RepositoryTest;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.vaga.entity.Vaga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class AnaliseConsultasRepositoryTest extends RepositoryTest {

    @Autowired
    private AnaliseRepository repository;

    private Usuario usuario;
    private Vaga vaga;

    @BeforeEach
    void setUp() {
        usuario = novoUsuario("ana@email.com");
        vaga = novaVaga(usuario);
    }

    private void salvar(Usuario dono, Vaga vagaDaAnalise, int versao, int nota, String... ausentes) {
        Curriculo curriculo = novoCurriculo(dono, "CV Backend", versao, dono.getEmail() + "-" + versao);
        Analise analise = new Analise(curriculo, vagaDaAnalise, nota, "Resumo", List.of(), List.of(),
                "ollama", "llama3.1", 100L);
        analise.adicionarPalavraChave("Java", TipoPalavraChave.PRESENTE);
        for (String termo : ausentes) {
            analise.adicionarPalavraChave(termo, TipoPalavraChave.AUSENTE);
        }
        repository.saveAndFlush(analise);
    }

    private void salvarTresAnalisesDaAna() {
        salvar(usuario, vaga, 1, 40, "Docker");
        salvar(usuario, vaga, 2, 70, "docker", "Kubernetes");
        salvar(usuario, vaga, 3, 85, "Kubernetes");
    }

    @Test
    void deveCalcularContagemMediaEMelhorNotaSomenteDoUsuario() {
        salvarTresAnalisesDaAna();
        Usuario outro = novoUsuario("outro@email.com");
        salvar(outro, novaVaga(outro), 1, 10, "Docker");

        assertThat(repository.countByCurriculoUsuarioId(usuario.getId())).isEqualTo(3);
        assertThat(repository.calcularNotaMedia(usuario.getId())).isEqualTo(65.0);
        assertThat(repository.buscarMelhorNota(usuario.getId())).isEqualTo(85);
    }

    @Test
    void usuarioSemAnalisesDeveTerEstatisticasNulas() {
        assertThat(repository.countByCurriculoUsuarioId(usuario.getId())).isZero();
        assertThat(repository.calcularNotaMedia(usuario.getId())).isNull();
        assertThat(repository.buscarMelhorNota(usuario.getId())).isNull();
    }

    @Test
    void deveTrazerAsUltimasNotasDaMaisRecenteParaAMaisAntiga() {
        salvarTresAnalisesDaAna();

        List<PontoEvolucaoView> pontos = repository.buscarUltimasNotas(usuario.getId(), PageRequest.of(0, 2));

        assertThat(pontos).extracting(PontoEvolucaoView::getNota).containsExactly(85, 70);
        assertThat(pontos.getFirst().getVersao()).isEqualTo(3);
        assertThat(pontos.getFirst().getCurriculoTitulo()).isEqualTo("CV Backend");
        assertThat(pontos.getFirst().getVagaTitulo()).isEqualTo(vaga.getTitulo());
        assertThat(pontos.getFirst().getCriadoEm()).isNotNull();
    }

    @Test
    void deveContarTermosAusentesIgnorandoMaiusculasEMinusculas() {
        salvarTresAnalisesDaAna();

        List<TermoFrequenteView> termos = repository.buscarTermosMaisFrequentes(
                usuario.getId(), TipoPalavraChave.AUSENTE, PageRequest.of(0, 10));

        assertThat(termos)
                .extracting(TermoFrequenteView::getTermo, TermoFrequenteView::getOcorrencias)
                .containsExactly(tuple("docker", 2L), tuple("kubernetes", 2L));
    }
}
