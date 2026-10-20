package br.com.cvmatch.dashboard.controller;

import br.com.cvmatch.dashboard.dto.DashboardResponse;
import br.com.cvmatch.dashboard.service.DashboardService;
import br.com.cvmatch.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Indicadores e evolução da nota")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Totais, médias, evolução da nota e palavras-chave que mais faltam")
    @GetMapping
    public DashboardResponse dashboard(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return dashboardService.montar(usuario.id());
    }
}
