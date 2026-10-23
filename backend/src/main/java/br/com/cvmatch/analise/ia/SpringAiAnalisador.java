package br.com.cvmatch.analise.ia;

import br.com.cvmatch.common.exception.FalhaIaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class SpringAiAnalisador implements AnalisadorIa {

    private final ChatModel chatModel;
    private final String provedor;
    private final String instrucoesSistema;
    private final BeanOutputConverter<AnaliseIaResponse> conversor = new BeanOutputConverter<>(AnaliseIaResponse.class);

    public SpringAiAnalisador(ChatModel chatModel,
                              @Value("${spring.ai.model.chat}") String provedor,
                              @Value("classpath:prompts/analise-sistema.txt") Resource instrucoes) {
        this.chatModel = chatModel;
        this.provedor = provedor;
        this.instrucoesSistema = lerTexto(instrucoes);
    }

    @Override
    public ResultadoAnaliseIa analisar(String textoCurriculo, String descricaoVaga) {
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(instrucoesSistema),
                new UserMessage(montarMensagem(textoCurriculo, descricaoVaga))));

        ChatResponse resposta = chamarModelo(prompt);
        String texto = resposta == null || resposta.getResult() == null
                ? null
                : resposta.getResult().getOutput().getText();

        if (!StringUtils.hasText(texto)) {
            throw new FalhaIaException("A IA retornou uma resposta vazia. Tente novamente.");
        }

        return new ResultadoAnaliseIa(converter(texto), provedor, modeloUsado(resposta));
    }

    private ChatResponse chamarModelo(Prompt prompt) {
        try {
            return chatModel.call(prompt);
        } catch (RuntimeException e) {
            log.error("Falha ao chamar o provedor de IA '{}'", provedor, e);
            throw new FalhaIaException("O serviço de IA está indisponível no momento. Tente novamente em instantes.");
        }
    }

    private AnaliseIaResponse converter(String texto) {
        try {
            AnaliseIaResponse analise = conversor.convert(texto);
            if (analise == null) {
                throw new IllegalStateException("Resposta convertida para nulo");
            }
            return analise;
        } catch (RuntimeException e) {
            log.warn("Resposta da IA em formato inválido: {}", e.getMessage());
            throw new FalhaIaException("A IA retornou uma resposta em formato inválido. Tente novamente.");
        }
    }

    private String modeloUsado(ChatResponse resposta) {
        if (resposta.getMetadata() != null && StringUtils.hasText(resposta.getMetadata().getModel())) {
            return resposta.getMetadata().getModel();
        }
        return "desconhecido";
    }

    private String montarMensagem(String textoCurriculo, String descricaoVaga) {
        return """
                Analise o currículo abaixo em relação à vaga.

                <vaga>
                %s
                </vaga>

                <curriculo>
                %s
                </curriculo>

                %s
                """.formatted(descricaoVaga, textoCurriculo, conversor.getFormat());
    }

    private static String lerTexto(Resource recurso) {
        try {
            return recurso.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler o prompt de sistema", e);
        }
    }
}
