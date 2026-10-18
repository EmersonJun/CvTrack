package br.com.cvmatch.usuario.dto;

import br.com.cvmatch.usuario.Role;

import java.util.UUID;

/** O que a API expõe do usuário. Nunca devolvemos a entidade (que tem o hash da senha). */
public record UsuarioResponse(UUID id, String nome, String email, Role role) {
}
