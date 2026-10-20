package br.com.cvmatch.analise.mapper;

import br.com.cvmatch.analise.dto.AnaliseResponse;
import br.com.cvmatch.analise.dto.AnaliseResumoResponse;
import br.com.cvmatch.analise.entity.Analise;
import br.com.cvmatch.analise.entity.PalavraChave;
import br.com.cvmatch.analise.entity.TipoPalavraChave;
import br.com.cvmatch.curriculo.entity.Curriculo;
import br.com.cvmatch.vaga.entity.Vaga;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = TipoPalavraChave.class)
public interface AnaliseMapper {

    @Mapping(target = "palavrasPresentes", expression = "java(termos(analise, TipoPalavraChave.PRESENTE))")
    @Mapping(target = "palavrasAusentes", expression = "java(termos(analise, TipoPalavraChave.AUSENTE))")
    AnaliseResponse paraResponse(Analise analise);

    AnaliseResumoResponse paraResumoAnalise(Analise analise);

    AnaliseResponse.CurriculoResumo paraResumo(Curriculo curriculo);

    AnaliseResponse.VagaResumo paraResumo(Vaga vaga);

    @Named("termos")
    default List<String> termos(Analise analise, TipoPalavraChave tipo) {
        return analise.getPalavrasChave().stream()
                .filter(palavra -> palavra.getTipo() == tipo)
                .map(PalavraChave::getTermo)
                .toList();
    }
}
