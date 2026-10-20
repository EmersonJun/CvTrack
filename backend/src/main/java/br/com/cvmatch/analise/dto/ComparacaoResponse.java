package br.com.cvmatch.analise.dto;

import java.util.List;

public record ComparacaoResponse(
        AnaliseResumoResponse anterior,
        AnaliseResumoResponse atual,
        int variacaoNota,
        List<String> palavrasConquistadas,
        List<String> palavrasPerdidas,
        List<String> palavrasAindaAusentes
) {
}
