package br.com.cvmatch.dashboard.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        long totalCurriculos,
        long totalVagas,
        long totalAnalises,
        Double notaMedia,
        Integer melhorNota,
        Integer ultimaNota,
        List<PontoEvolucao> evolucao,
        List<TermoFrequente> palavrasMaisAusentes
) {

    public record PontoEvolucao(
            UUID analiseId,
            int nota,
            Instant data,
            String curriculoTitulo,
            int versao,
            String vagaTitulo
    ) {
    }

    public record TermoFrequente(String termo, long ocorrencias) {
    }
}
