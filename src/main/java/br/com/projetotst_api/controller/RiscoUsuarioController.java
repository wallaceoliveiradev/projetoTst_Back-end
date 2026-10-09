package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.RiscoUsuarioResponse;
import br.com.projetotst_api.dto.RiscosUsuarioRequest;
import br.com.projetotst_api.service.RiscoUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class RiscoUsuarioController {

    private final RiscoUsuarioService riscoUsuarioService;

    public RiscoUsuarioController(RiscoUsuarioService riscoUsuarioService) {
        this.riscoUsuarioService = riscoUsuarioService;
    }

    @GetMapping("/me/riscos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<RiscoUsuarioResponse>> listarDoUsuarioAtual(Authentication authentication) {
        return ResponseEntity.ok(riscoUsuarioService.listarDoUsuario(authentication.getName()));
    }

    @GetMapping("/{usuarioId}/riscos")
    @Transactional(readOnly = true)
    public ResponseEntity<List<RiscoUsuarioResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(riscoUsuarioService.listarPorUsuarioId(usuarioId));
    }

    @PutMapping("/{usuarioId}/riscos")
    @Transactional
    public ResponseEntity<List<RiscoUsuarioResponse>> substituirAssociacoes(
            @PathVariable Long usuarioId,
            @Valid @RequestBody RiscosUsuarioRequest request) {
        return ResponseEntity.ok(
                riscoUsuarioService.substituirAssociacoes(usuarioId, request.riscos()));
    }
}
