package br.com.cvmatch.analise;

import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.analise.mapper.AnaliseMapper;
import br.com.cvmatch.analise.repository.AnaliseRepository;
import br.com.cvmatch.analise.service.ComparacaoService;
import br.com.cvmatch.common.exception.RegraNegocioException;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.vaga.entity.Vaga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComparacaoServiceTest {

    @Mock
    private AnaliseRepository analiseRepository;

    private ComparacaoService service;

    private final UUID usuarioId = UUID.randomUUID();
    private final UUID idVersao1 = UUID.randomUUID();
    private final UUID idVersao2 = UUID.randomUUID();
    private final Usuario usuario = new Usuario("Ana", "ana@email.com", "hash");
    private final Vaga vaga = new Vaga(usuario, "Dev Java", null, "Descrição");

    @BeforeEach
    void setUp() {
        service = new ComparacaoService(analiseRepository, Mappers.getMapper(AnaliseMapper.class));
    }

    private Analise analise(String titulo, int versao, Vaga vagaDaAnalise, int nota,
                            List<String> presentes, List<String> ausentes) {
        Curriculo curriculo = new Curriculo(usuario, titulo, versao, "cv.pdf", "texto", "hash-" + versao);
        Analise analise = new Analise(curriculo, vagaDaAnalise, nota, "Resumo", List.of(), List.of(),
                "ollama", "llama3.1", 100L);
        presentes.forEach(termo -> analise.adicionarPalavraChave(termo, TipoPalavraChave.PRESENTE));
        ausentes.forEach(termo -> analise.adicionarPalavraChave(termo, TipoPalavraChave.AUSENTE));
        return analise;
    }

    private void repositorioDevolve(Analise versao1, Analise versao2) {
        when(analiseRepository.findByIdAndCurriculoUsuarioId(idVersao1, usuarioId)).thenReturn(Optional.of(versao1));
        when(analiseRepository.findByIdAndCurriculoUsuarioId(idVersao2, usuarioId)).thenReturn(Optional.of(versao2));
    }

    @Test
    void deveCompararAsVersoesMesmoQuandoInformadasForaDeOrdem() {
        repositorioDevolve(
                analise("CV Backend", 1, vaga, 50, List.of("Java", "Git"), List.of("Docker", "Kubernetes")),
                analise("CV Backend", 2, vaga, 80, List.of("Java", "docker"), List.of("Kubernetes", "git")));

        var comparacao = service.comparar(usuarioId, idVersao2, idVersao1);

        assertThat(comparacao.anterior().curriculo().versao()).isEqualTo(1);
        assertThat(comparacao.atual().curriculo().versao()).isEqualTo(2);
        assertThat(comparacao.variacaoNota()).isEqualTo(30);
        assertThat(comparacao.palavrasConquistadas()).containsExactly("docker");
        assertThat(comparacao.palavrasPerdidas()).containsExactly("git");
        assertThat(comparacao.palavrasAindaAusentes()).containsExactly("Kubernetes");
    }

    @Test
    void naoDeveCompararAnalisesDeVagasDiferentes() {
        Vaga outraVaga = new Vaga(usuario, "Dev Python", null, "Outra descrição");
        repositorioDevolve(
                analise("CV Backend", 1, vaga, 50, List.of(), List.of()),
                analise("CV Backend", 2, outraVaga, 80, List.of(), List.of()));

        assertThatThrownBy(() -> service.comparar(usuarioId, idVersao1, idVersao2))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void naoDeveCompararCurriculosDiferentes() {
        repositorioDevolve(
                analise("CV Backend", 1, vaga, 50, List.of(), List.of()),
                analise("CV Frontend", 1, vaga, 80, List.of(), List.of()));

        assertThatThrownBy(() -> service.comparar(usuarioId, idVersao1, idVersao2))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void naoDeveCompararUmaAnaliseComElaMesma() {
        assertThatThrownBy(() -> service.comparar(usuarioId, idVersao1, idVersao1))
                .isInstanceOf(RegraNegocioException.class);

        verifyNoInteractions(analiseRepository);
    }
}
