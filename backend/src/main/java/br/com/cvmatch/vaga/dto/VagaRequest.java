package br.com.cvmatch.vaga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VagaRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @Size(max = 150, message = "A empresa deve ter no máximo 150 caracteres")
        String empresa,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 50, max = 20000, message = "A descrição deve ter entre 50 e 20000 caracteres")
        String descricao
) {
}
