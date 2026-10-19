package br.com.cvmatch.curriculo.controller;

import br.com.cvmatch.curriculo.dto.CurriculoDetalheResponse;
import br.com.cvmatch.curriculo.dto.CurriculoResponse;
import br.com.cvmatch.curriculo.service.CurriculoService;
import br.com.cvmatch.security.UsuarioAutenticado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/curriculos")
@RequiredArgsConstructor
public class CurriculoController {

    private final CurriculoService curriculoService;

    /**
     * Upload via multipart/form-data (o formato que formulários usam para arquivos).
     * As anotações @NotBlank/@Size nos parâmetros são validadas pelo próprio Spring MVC (6.1+).
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CurriculoResponse> enviar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @RequestParam("titulo")
            @NotBlank(message = "O título é obrigatório")
            @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
            String titulo,
            @RequestPart("arquivo") MultipartFile arquivo) {

        CurriculoResponse criado = curriculoService.enviar(usuario.id(), titulo, arquivo);

        // 201 Created + cabeçalho Location apontando para o novo recurso (boa prática REST)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<CurriculoResponse> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return curriculoService.listar(usuario.id());
    }

    /** Todas as versões de um mesmo currículo. Ex.: GET /api/curriculos/versoes?titulo=CV Backend */
    @GetMapping("/versoes")
    public List<CurriculoResponse> listarVersoes(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                                 @RequestParam("titulo") String titulo) {
        return curriculoService.listarVersoes(usuario.id(), titulo);
    }

    @GetMapping("/{id}")
    public CurriculoDetalheResponse buscar(@AuthenticationPrincipal UsuarioAutenticado usuario,
                                           @PathVariable("id") UUID id) {
        return curriculoService.buscar(id, usuario.id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioAutenticado usuario,
                        @PathVariable("id") UUID id) {
        curriculoService.excluir(id, usuario.id());
    }
}
