package br.com.cvmatch.curriculo.repository;

import br.com.cvmatch.curriculo.entity.Curriculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurriculoRepository extends JpaRepository<Curriculo, UUID> {

    Optional<Curriculo> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    List<Curriculo> findAllByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    List<Curriculo> findAllByUsuarioIdAndTituloOrderByVersaoAsc(UUID usuarioId, String titulo);

    @Query("""
            select coalesce(max(c.versao), 0)
            from Curriculo c
            where c.usuario.id = :usuarioId and c.titulo = :titulo
            """)
    int buscarUltimaVersao(@Param("usuarioId") UUID usuarioId, @Param("titulo") String titulo);

    boolean existsByUsuarioIdAndHashConteudo(UUID usuarioId, String hashConteudo);

    long countByUsuarioId(UUID usuarioId);
}
