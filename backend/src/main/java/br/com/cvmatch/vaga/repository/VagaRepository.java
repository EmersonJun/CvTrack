package br.com.cvmatch.vaga.repository;

import br.com.cvmatch.vaga.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VagaRepository extends JpaRepository<Vaga, UUID> {

    Optional<Vaga> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    List<Vaga> findAllByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    long countByUsuarioId(UUID usuarioId);
}
