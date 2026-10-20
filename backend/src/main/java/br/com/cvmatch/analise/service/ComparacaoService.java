package br.com.cvmatch.analise.service;

import br.com.cvmatch.analise.dto.ComparacaoResponse;
import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.PalavraChave;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.analise.mapper.AnaliseMapper;
import br.com.cvmatch.analise.repository.AnaliseRepository;
import br.com.cvmatch.common.exception.RecursoNaoEncontradoException;
import br.com.cvmatch.common.exception.RegraNegocioException;
import br.com.cvmatch.vaga.entity.Vaga;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComparacaoService {

    private static final Comparator<Analise> MAIS_ANTIGA_PRIMEIRO = Comparator
            .comparingInt((Analise analise) -> analise.getCurriculo().getVersao())
            .thenComparing(Analise::getCriadoEm, Comparator.nullsLast(Comparator.naturalOrder()));

    private final AnaliseRepository analiseRepository;
    private final AnaliseMapper mapper;

    @Transactional(readOnly = true)
    public ComparacaoResponse comparar(UUID usuarioId, UUID primeiraId, UUID segundaId) {
        if (primeiraId.equals(segundaId)) {
            throw new RegraNegocioException("Escolha duas análises diferentes para comparar");
        }

        Analise primeira = buscar(primeiraId, usuarioId);
        Analise segunda = buscar(segundaId, usuarioId);

        if (!mesmoCurriculo(primeira, segunda) || !mesmaVaga(primeira.getVaga(), segunda.getVaga())) {
            throw new RegraNegocioException(
                    "Só é possível comparar versões do mesmo currículo analisadas para a mesma vaga");
        }

        Analise anterior = MAIS_ANTIGA_PRIMEIRO.compare(primeira, segunda) <= 0 ? primeira : segunda;
        Analise atual = anterior == primeira ? segunda : primeira;

        Map<String, String> presentesAntes = termos(anterior, TipoPalavraChave.PRESENTE);
        Map<String, String> ausentesAntes = termos(anterior, TipoPalavraChave.AUSENTE);
        Map<String, String> presentesAgora = termos(atual, TipoPalavraChave.PRESENTE);
        Map<String, String> ausentesAgora = termos(atual, TipoPalavraChave.AUSENTE);

        return new ComparacaoResponse(
                mapper.paraResumoAnalise(anterior),
                mapper.paraResumoAnalise(atual),
                atual.getNota() - anterior.getNota(),
                emAmbos(presentesAgora, ausentesAntes),
                emAmbos(ausentesAgora, presentesAntes),
                emAmbos(ausentesAgora, ausentesAntes));
    }

    private Analise buscar(UUID id, UUID usuarioId) {
        return analiseRepository.findByIdAndCurriculoUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Análise não encontrada"));
    }

    private boolean mesmoCurriculo(Analise a, Analise b) {
        return a.getCurriculo().getTitulo().equals(b.getCurriculo().getTitulo());
    }

    private boolean mesmaVaga(Vaga a, Vaga b) {
        return a == b || (a.getId() != null && a.getId().equals(b.getId()));
    }

    private Map<String, String> termos(Analise analise, TipoPalavraChave tipo) {
        Map<String, String> termos = new LinkedHashMap<>();
        analise.getPalavrasChave().stream()
                .filter(palavra -> palavra.getTipo() == tipo)
                .map(PalavraChave::getTermo)
                .forEach(termo -> termos.putIfAbsent(termo.toLowerCase(Locale.ROOT), termo));
        return termos;
    }

    private List<String> emAmbos(Map<String, String> base, Map<String, String> outro) {
        return base.entrySet().stream()
                .filter(termo -> outro.containsKey(termo.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }
}
