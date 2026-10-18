package br.com.cvmatch.usuario;

import br.com.cvmatch.support.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioRepositoryTest extends RepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void deveSalvarComEmailNormalizadoERolePadrao() {
        repository.saveAndFlush(new Usuario("Ana", "  Ana@Email.COM ", "hash"));
        em.clear(); // força a próxima leitura a vir do banco, não do cache

        var encontrado = repository.findByEmail("ana@email.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isNotNull();
        assertThat(encontrado.get().getCriadoEm()).isNotNull();
        assertThat(encontrado.get().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void naoDevePermitirEmailDuplicado() {
        repository.saveAndFlush(new Usuario("Ana", "ana@email.com", "hash"));

        assertThatThrownBy(() -> repository.saveAndFlush(new Usuario("Outra Ana", "ana@email.com", "hash")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void existsByEmailDeveIndicarSeEmailJaFoiUsado() {
        novoUsuario("bruno@email.com");

        assertThat(repository.existsByEmail("bruno@email.com")).isTrue();
        assertThat(repository.existsByEmail("nao.existe@email.com")).isFalse();
    }
}
