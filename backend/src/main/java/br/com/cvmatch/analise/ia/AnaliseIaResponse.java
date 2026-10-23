package br.com.cvmatch.analise.ia;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record AnaliseIaResponse(
        @JsonPropertyDescription("Nota de aderência do currículo à vaga, de 0 a 100")
        Integer nota,

        @JsonPropertyDescription("Resumo da avaliação em até 3 frases")
        String resumo,

        @JsonPropertyDescription("Pontos fortes do candidato em relação à vaga")
        List<String> pontosFortes,

        @JsonPropertyDescription("Sugestões objetivas para melhorar o currículo para esta vaga")
        List<String> sugestoes,

        @JsonPropertyDescription("Palavras-chave da vaga que aparecem no currículo")
        List<String> palavrasChavePresentes,

        @JsonPropertyDescription("Palavras-chave importantes da vaga que não aparecem no currículo")
        List<String> palavrasChaveAusentes
) {
}
