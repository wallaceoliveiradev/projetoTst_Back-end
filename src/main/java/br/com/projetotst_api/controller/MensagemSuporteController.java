package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.MensagemSuporteRequest;
import br.com.projetotst_api.dto.MensagemSuporteResponse;
import br.com.projetotst_api.service.MensagemSuporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suporte")
public class MensagemSuporteController {

    private final MensagemSuporteService service;

    public MensagemSuporteController(MensagemSuporteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MensagemSuporteResponse> criar(
            Authentication authentication,
            @Valid @RequestBody MensagemSuporteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.criar(authentication.getName(), request));
    }

    @GetMapping("/admin")
    public List<MensagemSuporteResponse> listarParaAdmin() {
        return service.listarParaAdmin();
    }

    @PatchMapping("/admin/{id}/visualizada")
    public MensagemSuporteResponse marcarComoVisualizada(@PathVariable Long id) {
        return service.marcarComoVisualizada(id);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
