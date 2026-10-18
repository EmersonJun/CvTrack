package br.com.cvmatch.usuario.mapper;

import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.dto.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * O MapStruct gera a implementação (UsuarioMapperImpl) em tempo de COMPILAÇÃO:
 * sem reflexão em tempo de execução, e campos com nomes diferentes viram erro de build.
 * Veja o código gerado em target/generated-sources/annotations.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {

    UsuarioResponse paraResponse(Usuario usuario);
}
