package br.com.cvmatch.dashboard.controller;

import br.com.cvmatch.dashboard.dto.DashboardResponse;
import br.com.cvmatch.dashboard.service.DashboardService;
import br.com.cvmatch.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse dashboard(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return dashboardService.montar(usuario.id());
    }
}
