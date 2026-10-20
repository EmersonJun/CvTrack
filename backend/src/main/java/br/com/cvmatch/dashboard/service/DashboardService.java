package br.com.cvmatch.dashboard.service;

import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.analise.repository.AnaliseRepository;
import br.com.cvmatch.analise.repository.PontoEvolucaoView;
import br.com.cvmatch.curriculo.repository.CurriculoRepository;
import br.com.cvmatch.dashboard.dto.DashboardResponse;
import br.com.cvmatch.dashboard.dto.DashboardResponse.PontoEvolucao;
import br.com.cvmatch.dashboard.dto.DashboardResponse.TermoFrequente;
import br.com.cvmatch.vaga.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    static final int PONTOS_NO_GRAFICO = 30;
    static final int TERMOS_NO_RANKING = 10;

    private final AnaliseRepository analiseRepository;
    private final CurriculoRepository curriculoRepository;
    private final VagaRepository vagaRepository;

    @Transactional(readOnly = true)
    public DashboardResponse montar(UUID usuarioId) {
        List<PontoEvolucao> evolucao = analiseRepository
                .buscarUltimasNotas(usuarioId, PageRequest.of(0, PONTOS_NO_GRAFICO))
                .reversed()
                .stream()
                .map(this::paraPonto)
                .toList();

        List<TermoFrequente> maisAusentes = analiseRepository
                .buscarTermosMaisFrequentes(usuarioId, TipoPalavraChave.AUSENTE, PageRequest.of(0, TERMOS_NO_RANKING))
                .stream()
                .map(termo -> new TermoFrequente(termo.getTermo(), termo.getOcorrencias()))
                .toList();

        return new DashboardResponse(
                curriculoRepository.countByUsuarioId(usuarioId),
                vagaRepository.countByUsuarioId(usuarioId),
                analiseRepository.countByCurriculoUsuarioId(usuarioId),
                arredondar(analiseRepository.calcularNotaMedia(usuarioId)),
                analiseRepository.buscarMelhorNota(usuarioId),
                evolucao.isEmpty() ? null : evolucao.getLast().nota(),
                evolucao,
                maisAusentes);
    }

    private PontoEvolucao paraPonto(PontoEvolucaoView ponto) {
        return new PontoEvolucao(
                ponto.getAnaliseId(),
                ponto.getNota(),
                ponto.getCriadoEm(),
                ponto.getCurriculoTitulo(),
                ponto.getVersao(),
                ponto.getVagaTitulo());
    }

    static Double arredondar(Double valor) {
        return valor == null ? null : Math.round(valor * 10) / 10.0;
    }
}
