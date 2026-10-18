package br.com.cvmatch.analise.repository;

import br.com.cvmatch.analise.entity.Analise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Não existe PalavraChaveRepository: palavra-chave faz parte do agregado
 * Analise e é salva em cascata por ele.
 */
public interface AnaliseRepository extends JpaRepository<Analise, UUID> {

    /**
     * Detalhe da análise. O @EntityGraph traz currículo, vaga e palavras-chave
     * em UMA consulta (JOIN FETCH), evitando o problema N+1.
     */
    @EntityGraph(attributePaths = {"curriculo", "vaga", "palavrasChave"})
    Optional<Analise> findByIdAndCurriculoUsuarioId(UUID id, UUID usuarioId);

    /** Histórico paginado. Só relações *ToOne no graph: coleções + paginação não combinam. */
    @EntityGraph(attributePaths = {"curriculo", "vaga"})
    Page<Analise> findAllByCurriculoUsuarioId(UUID usuarioId, Pageable pageable);
}
