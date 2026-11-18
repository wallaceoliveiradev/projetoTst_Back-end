package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.DashboardResponse;
import br.com.projetotst_api.service.IndicadoresService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final IndicadoresService service;

    public DashboardController(IndicadoresService service) {
        this.service = service;
    }

    @GetMapping("/api/dashboard")
    public DashboardResponse obter(
            @RequestParam(defaultValue = "30") int diasAlertaEpi) {
        return service.obterDashboard(diasAlertaEpi);
    }
}
