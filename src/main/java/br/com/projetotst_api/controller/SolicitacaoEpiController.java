package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.SolicitacaoEpiRequest;
import br.com.projetotst_api.dto.SolicitacaoEpiResponse;
import br.com.projetotst_api.service.SolicitacaoEpiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes-epis")
public class SolicitacaoEpiController {
    private final SolicitacaoEpiService service;

    public SolicitacaoEpiController(SolicitacaoEpiService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoEpiResponse> criar(
            Authentication authentication, @Valid @RequestBody SolicitacaoEpiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(authentication.getName(), request));
    }

    @GetMapping("/admin")
    public List<SolicitacaoEpiResponse> listarParaAdmin() {
        return service.listarParaAdmin();
    }

    @PatchMapping("/admin/{id}/visualizada")
    public SolicitacaoEpiResponse marcarComoVisualizada(@PathVariable Long id) {
        return service.marcarComoVisualizada(id);
    }

    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
