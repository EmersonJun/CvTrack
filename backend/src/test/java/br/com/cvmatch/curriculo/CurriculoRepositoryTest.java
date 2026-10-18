package br.com.cvmatch.curriculo;

import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.curriculo.repository.CurriculoRepository;
import br.com.cvmatch.support.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurriculoRepositoryTest extends RepositoryTest {

    @Autowired
    private CurriculoRepository repository;

    @Test
    void buscarUltimaVersaoDeveRetornarZeroQuandoNaoHaVersoes() {
        var usuario = novoUsuario("ana@email.com");

        assertThat(repository.buscarUltimaVersao(usuario.getId(), "CV Backend")).isZero();
    }

    @Test
    void buscarUltimaVersaoDeveRetornarAMaiorVersaoDoTitulo() {
        var usuario = novoUsuario("ana@email.com");
        novoCurriculo(usuario, "CV Backend", 1, "hash-1");
        novoCurriculo(usuario, "CV Backend", 2, "hash-2");
        novoCurriculo(usuario, "CV Frontend", 5, "hash-3");

        assertThat(repository.buscarUltimaVersao(usuario.getId(), "CV Backend")).isEqualTo(2);
    }

    @Test
    void naoDeveRetornarCurriculoDeOutroUsuario() {
        var dono = novoUsuario("dono@email.com");
        var intruso = novoUsuario("intruso@email.com");
        var curriculo = novoCurriculo(dono, "CV", 1, "hash-1");

        assertThat(repository.findByIdAndUsuarioId(curriculo.getId(), dono.getId())).isPresent();
        assertThat(repository.findByIdAndUsuarioId(curriculo.getId(), intruso.getId())).isEmpty();
    }

    @Test
    void deveListarVersoesDoMesmoCurriculoEmOrdem() {
        var usuario = novoUsuario("ana@email.com");
        novoCurriculo(usuario, "CV", 2, "hash-2");
        novoCurriculo(usuario, "CV", 1, "hash-1");

        var versoes = repository.findAllByUsuarioIdAndTituloOrderByVersaoAsc(usuario.getId(), "CV");

        assertThat(versoes).extracting(Curriculo::getVersao).containsExactly(1, 2);
    }

    @Test
    void deveDetectarArquivoJaEnviado() {
        var usuario = novoUsuario("ana@email.com");
        novoCurriculo(usuario, "CV", 1, "hash-abc");

        assertThat(repository.existsByUsuarioIdAndHashConteudo(usuario.getId(), "hash-abc")).isTrue();
        assertThat(repository.existsByUsuarioIdAndHashConteudo(usuario.getId(), "hash-xyz")).isFalse();
    }

    @Test
    void naoDevePermitirVersaoDuplicadaParaOMesmoTitulo() {
        var usuario = novoUsuario("ana@email.com");
        repository.saveAndFlush(new Curriculo(usuario, "CV", 1, "a.pdf", "texto a", "hash-a"));

        assertThatThrownBy(() -> repository.saveAndFlush(
                new Curriculo(usuario, "CV", 1, "b.pdf", "texto b", "hash-b")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
