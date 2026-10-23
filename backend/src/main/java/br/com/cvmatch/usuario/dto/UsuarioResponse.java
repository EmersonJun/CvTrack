package br.com.cvmatch.usuario.dto;

import br.com.cvmatch.usuario.Role;

import java.util.UUID;

public record UsuarioResponse(UUID id, String nome, String email, Role role) {
}
