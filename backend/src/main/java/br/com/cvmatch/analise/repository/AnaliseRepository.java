package br.com.cvmatch.analise.repository;

import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnaliseRepository extends JpaRepository<Analise, UUID> {

    @EntityGraph(attributePaths = {"curriculo", "vaga", "palavrasChave"})
    Optional<Analise> findByIdAndCurriculoUsuarioId(UUID id, UUID usuarioId);

    @EntityGraph(attributePaths = {"curriculo", "vaga"})
    Page<Analise> findAllByCurriculoUsuarioId(UUID usuarioId, Pageable pageable);

    long countByCurriculoUsuarioId(UUID usuarioId);

    @Query("select avg(a.nota) from Analise a where a.curriculo.usuario.id = :usuarioId")
    Double calcularNotaMedia(@Param("usuarioId") UUID usuarioId);

    @Query("select max(a.nota) from Analise a where a.curriculo.usuario.id = :usuarioId")
    Integer buscarMelhorNota(@Param("usuarioId") UUID usuarioId);

    @Query("""
            select a.id as analiseId,
                   a.nota as nota,
                   a.criadoEm as criadoEm,
                   c.titulo as curriculoTitulo,
                   c.versao as versao,
                   v.titulo as vagaTitulo
            from Analise a
            join a.curriculo c
            join a.vaga v
            where c.usuario.id = :usuarioId
            order by a.criadoEm desc
            """)
    List<PontoEvolucaoView> buscarUltimasNotas(@Param("usuarioId") UUID usuarioId, Pageable limite);

    @Query("""
            select lower(p.termo) as termo, count(p) as ocorrencias
            from PalavraChave p
            where p.analise.curriculo.usuario.id = :usuarioId and p.tipo = :tipo
            group by lower(p.termo)
            order by count(p) desc, lower(p.termo) asc
            """)
    List<TermoFrequenteView> buscarTermosMaisFrequentes(@Param("usuarioId") UUID usuarioId,
                                                        @Param("tipo") TipoPalavraChave tipo,
                                                        Pageable limite);
}
