package br.com.cvmatch.usuario.mapper;

import br.com.cvmatch.usuario.Usuario;
import br.com.cvmatch.usuario.dto.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {

    UsuarioResponse paraResponse(Usuario usuario);
}
