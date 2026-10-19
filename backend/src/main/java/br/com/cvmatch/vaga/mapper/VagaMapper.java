package br.com.cvmatch.vaga.mapper;

import br.com.cvmatch.vaga.dto.VagaResponse;
import br.com.cvmatch.vaga.entity.Vaga;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VagaMapper {

    VagaResponse paraResponse(Vaga vaga);

    List<VagaResponse> paraResponses(List<Vaga> vagas);
}
