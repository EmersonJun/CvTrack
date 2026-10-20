package br.com.cvmatch.analise.controller;

import br.com.cvmatch.analise.dto.AnaliseResponse;
import br.com.cvmatch.analise.dto.AnaliseResumoResponse;
import br.com.cvmatch.analise.dto.ComparacaoResponse;
import br.com.cvmatch.analise.dto.NovaAnaliseRequest;
import br.com.cvmatch.analise.service.AnaliseService;
import br.com.cvmatch.analise.service.ComparacaoService;
import br.com.cvmatch.common.dto.PaginaResponse;
import br.com.cvmatch.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/analises")
@RequiredArgsConstructor
public class AnaliseController {

    private final AnaliseService analiseService;
    private final ComparacaoService comparacaoService;

    @PostMapping
    public ResponseEntity<AnaliseResponse> analisar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                    @Valid @RequestBody NovaAnaliseRequest requisicao) {
        AnaliseResponse analise = analiseService.analisar(usuario.id(), requisicao);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(analise.id())
                .toUri();
        return ResponseEntity.created(location).body(analise);
    }

    @GetMapping
    public PaginaResponse<AnaliseResumoResponse> listar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam(name = "pagina", defaultValue = "0")
            @Min(value = 0, message = "A página não pode ser negativa")
            int pagina,
            @RequestParam(name = "tamanho", defaultValue = "10")
            @Min(value = 1, message = "O tamanho mínimo é 1")
            @Max(value = 50, message = "O tamanho máximo é 50")
            int tamanho) {
        return analiseService.listar(usuario.id(), pagina, tamanho);
    }

    @GetMapping("/comparacao")
    public ComparacaoResponse comparar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                       @RequestParam("anteriorId") UUID anteriorId,
                                       @RequestParam("atualId") UUID atualId) {
        return comparacaoService.comparar(usuario.id(), anteriorId, atualId);
    }

    @GetMapping("/{id}")
    public AnaliseResponse buscar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                  @PathVariable("id") UUID id) {
        return analiseService.buscar(id, usuario.id());
    }
}
