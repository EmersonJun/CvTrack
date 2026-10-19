package br.com.cvmatch.curriculo.mapper;

import br.com.cvmatch.curriculo.dto.CurriculoDetalheResponse;
import br.com.cvmatch.curriculo.dto.CurriculoResponse;
import br.com.cvmatch.curriculo.entity.Curriculo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CurriculoMapper {

    // Campos com o mesmo nome são copiados sozinhos; só o calculado precisa de regra
    @Mapping(target = "totalCaracteres", expression = "java(curriculo.getTextoExtraido().length())")
    CurriculoResponse paraResponse(Curriculo curriculo);

    List<CurriculoResponse> paraResponses(List<Curriculo> curriculos);

    @Mapping(target = "totalCaracteres", expression = "java(curriculo.getTextoExtraido().length())")
    CurriculoDetalheResponse paraDetalhe(Curriculo curriculo);
}
