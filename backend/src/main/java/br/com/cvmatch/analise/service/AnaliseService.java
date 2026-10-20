package br.com.cvmatch.analise.service;

import br.com.cvmatch.analise.dto.AnaliseResponse;
import br.com.cvmatch.analise.dto.AnaliseResumoResponse;
import br.com.cvmatch.analise.dto.NovaAnaliseRequest;
import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.analise.ia.AnalisadorIa;
import br.com.cvmatch.analise.ia.AnaliseIaResponse;
import br.com.cvmatch.analise.ia.ResultadoAnaliseIa;
import br.com.cvmatch.analise.mapper.AnaliseMapper;
import br.com.cvmatch.analise.repository.AnaliseRepository;
import br.com.cvmatch.common.dto.PaginaResponse;
import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.curriculo.repository.CurriculoRepository;
import br.com.cvmatch.vaga.entity.Vaga;
import br.com.cvmatch.vaga.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnaliseService {

    static final int MAXIMO_ITENS = 15;
    static final int TAMANHO_MAXIMO_TERMO = 100;
    static final int TAMANHO_MAXIMO_ITEM = 500;
    static final int TAMANHO_MAXIMO_RESUMO = 2000;

    private final AnaliseRepository analiseRepository;
    private final CurriculoRepository curriculoRepository;
    private final VagaRepository vagaRepository;
    private final AnalisadorIa analisadorIa;
    private final AnaliseMapper mapper;

    public AnaliseResponse analisar(UUID usuarioId, NovaAnaliseRequest requisicao) {
        Curriculo curriculo = curriculoRepository.findByIdAndUsuarioId(requisicao.curriculoId(), usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Currículo não encontrado"));
        Vaga vaga = vagaRepository.findByIdAndUsuarioId(requisicao.vagaId(), usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada"));

        long inicio = System.currentTimeMillis();
        ResultadoAnaliseIa resultado = analisadorIa.analisar(curriculo.getTextoExtraido(), vaga.getDescricao());
        long tempoMs = System.currentTimeMillis() - inicio;

        Analise analise = montarAnalise(curriculo, vaga, resultado, tempoMs);
        return mapper.paraResponse(analiseRepository.saveAndFlush(analise));
    }

    @Transactional(readOnly = true)
    public AnaliseResponse buscar(UUID id, UUID usuarioId) {
        return analiseRepository.findByIdAndCurriculoUsuarioId(id, usuarioId)
                .map(mapper::paraResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Análise não encontrada"));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<AnaliseResumoResponse> listar(UUID usuarioId, int pagina, int tamanho) {
        PageRequest paginacao = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "criadoEm"));
        return PaginaResponse.de(analiseRepository.findAllByCurriculoUsuarioId(usuarioId, paginacao),
                mapper::paraResumoAnalise);
    }

    private Analise montarAnalise(Curriculo curriculo, Vaga vaga, ResultadoAnaliseIa resultado, long tempoMs) {
        AnaliseIaResponse ia = resultado.resposta();

        Analise analise = new Analise(
                curriculo,
                vaga,
                limitarNota(ia.nota()),
                limitar(ia.resumo(), TAMANHO_MAXIMO_RESUMO),
                limparLista(ia.pontosFortes(), TAMANHO_MAXIMO_ITEM),
                limparLista(ia.sugestoes(), TAMANHO_MAXIMO_ITEM),
                limitar(resultado.provedor(), 30),
                limitar(resultado.modelo(), 100),
                tempoMs);

        List<String> presentes = limparLista(ia.palavrasChavePresentes(), TAMANHO_MAXIMO_TERMO);
        Set<String> presentesNormalizados = presentes.stream()
                .map(termo -> termo.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        presentes.forEach(termo -> analise.adicionarPalavraChave(termo, TipoPalavraChave.PRESENTE));
        limparLista(ia.palavrasChaveAusentes(), TAMANHO_MAXIMO_TERMO).stream()
                .filter(termo -> !presentesNormalizados.contains(termo.toLowerCase(Locale.ROOT)))
                .forEach(termo -> analise.adicionarPalavraChave(termo, TipoPalavraChave.AUSENTE));

        return analise;
    }

    static int limitarNota(Integer nota) {
        if (nota == null) {
            return 0;
        }
        return Math.max(0, Math.min(100, nota));
    }

    static List<String> limparLista(List<String> itens, int tamanhoMaximo) {
        if (itens == null) {
            return List.of();
        }
        Map<String, String> unicos = new LinkedHashMap<>();
        for (String item : itens) {
            if (StringUtils.hasText(item)) {
                String limpo = limitar(item.strip(), tamanhoMaximo);
                unicos.putIfAbsent(limpo.toLowerCase(Locale.ROOT), limpo);
            }
        }
        return unicos.values().stream().limit(MAXIMO_ITENS).toList();
    }

    static String limitar(String texto, int tamanhoMaximo) {
        if (texto == null) {
            return null;
        }
        String limpo = texto.strip();
        return limpo.length() > tamanhoMaximo ? limpo.substring(0, tamanhoMaximo) : limpo;
    }
}
