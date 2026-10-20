package br.com.cvmatch.vaga.controller;

import br.com.cvmatch.security.UsuarioAutenticado;
import br.com.cvmatch.vaga.dto.VagaRequest;
import br.com.cvmatch.vaga.dto.VagaResponse;
import br.com.cvmatch.vaga.service.VagaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Vagas", description = "Cadastro das vagas que serão analisadas")
@RestController
@RequestMapping("/api/vagas")
@RequiredArgsConstructor
public class VagaController {

    private final VagaService vagaService;

    @Operation(summary = "Cadastra uma vaga")
    @PostMapping
    public ResponseEntity<VagaResponse> criar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                              @Valid @RequestBody VagaRequest requisicao) {
        VagaResponse criada = vagaService.criar(usuario.id(), requisicao);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Lista as vagas do usuário")
    @GetMapping
    public List<VagaResponse> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return vagaService.listar(usuario.id());
    }

    @Operation(summary = "Detalha uma vaga")
    @GetMapping("/{id}")
    public VagaResponse buscar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                               @PathVariable("id") UUID id) {
        return vagaService.buscar(id, usuario.id());
    }

    @Operation(summary = "Atualiza uma vaga")
    @PutMapping("/{id}")
    public VagaResponse atualizar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                  @PathVariable("id") UUID id,
                                  @Valid @RequestBody VagaRequest requisicao) {
        return vagaService.atualizar(id, usuario.id(), requisicao);
    }

    @Operation(summary = "Exclui uma vaga e as análises dela")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioAutenticado usuario,
                        @PathVariable("id") UUID id) {
        vagaService.excluir(id, usuario.id());
    }
}
