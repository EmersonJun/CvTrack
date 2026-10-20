package br.com.cvmatch.analise.dto;

import java.time.Instant;
import java.util.UUID;

public record AnaliseResumoResponse(
        UUID id,
        int nota,
        AnaliseResponse.CurriculoResumo curriculo,
        AnaliseResponse.VagaResumo vaga,
        Instant criadoEm
) {
}
