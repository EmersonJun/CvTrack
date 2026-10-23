package br.com.cvmatch.analise.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AnaliseResponse(
        UUID id,
        int nota,
        String resumo,
        List<String> pontosFortes,
        List<String> sugestoes,
        List<String> palavrasPresentes,
        List<String> palavrasAusentes,
        CurriculoResumo curriculo,
        VagaResumo vaga,
        String provedorIa,
        String modelo,
        Long tempoProcessamentoMs,
        Instant criadoEm
) {

    public record CurriculoResumo(UUID id, String titulo, int versao) {
    }

    public record VagaResumo(UUID id, String titulo, String empresa) {
    }
}
