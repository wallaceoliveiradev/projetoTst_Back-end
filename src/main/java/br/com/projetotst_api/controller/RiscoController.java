package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.RiscoRequest;
import br.com.projetotst_api.dto.RiscoResponse;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import br.com.projetotst_api.service.RiscoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/riscos")
public class RiscoController {

    private final RiscoService riscoService;

    public RiscoController(RiscoService riscoService) {
        this.riscoService = riscoService;
    }

    @GetMapping
    public ResponseEntity<List<RiscoResponse>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) NivelRisco nivel,
            @RequestParam(required = false) SituacaoRisco situacao) {
        return ResponseEntity.ok(riscoService.listar(busca, nivel, situacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiscoResponse> obter(@PathVariable Long id) {
        return ResponseEntity.ok(riscoService.obter(id));
    }

    @PostMapping
    public ResponseEntity<RiscoResponse> criar(@Valid @RequestBody RiscoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(riscoService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RiscoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RiscoRequest request) {
        return ResponseEntity.ok(riscoService.atualizar(id, request));
    }
}
