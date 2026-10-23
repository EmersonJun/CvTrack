package br.com.cvmatch.analise.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NovaAnaliseRequest(
        @NotNull(message = "O currículo é obrigatório")
        UUID curriculoId,

        @NotNull(message = "A vaga é obrigatória")
        UUID vagaId
) {
}
