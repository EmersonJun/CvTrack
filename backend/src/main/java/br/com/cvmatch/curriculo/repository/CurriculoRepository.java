package br.com.cvmatch.curriculo.repository;

import br.com.cvmatch.curriculo.entity.Curriculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurriculoRepository extends JpaRepository<Curriculo, UUID> {

    /**
     * Busca SEMPRE filtrando pelo dono. Se o id for de outro usuário,
     * o resultado é vazio: o service responde 404 sem revelar que o recurso existe.
     */
    Optional<Curriculo> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    List<Curriculo> findAllByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    /** Todas as versões de um mesmo currículo, da mais antiga para a mais nova. */
    List<Curriculo> findAllByUsuarioIdAndTituloOrderByVersaoAsc(UUID usuarioId, String titulo);

    /** Retorna 0 quando ainda não há versões; o próximo upload será a versão (resultado + 1). */
    @Query("""
            select coalesce(max(c.versao), 0)
            from Curriculo c
            where c.usuario.id = :usuarioId and c.titulo = :titulo
            """)
    int buscarUltimaVersao(@Param("usuarioId") UUID usuarioId, @Param("titulo") String titulo);

    boolean existsByUsuarioIdAndHashConteudo(UUID usuarioId, String hashConteudo);
}
