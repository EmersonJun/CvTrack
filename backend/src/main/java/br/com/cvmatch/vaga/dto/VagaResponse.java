package br.com.cvmatch.vaga.dto;

import java.time.Instant;
import java.util.UUID;

public record VagaResponse(
        UUID id,
        String titulo,
        String empresa,
        String descricao,
        Instant criadoEm
) {
}
