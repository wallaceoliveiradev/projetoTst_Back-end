package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.UsuarioResponse;
import br.com.projetotst_api.dto.LoginRequest;
import br.com.projetotst_api.dto.LoginResponse;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Usuario usuario = authService.autenticar(request.email(), request.senha());
            String token = authService.gerarToken(request.email(), request.senha());
            return ResponseEntity.ok(new LoginResponse(token, authService.expiracaoEmSegundos(),
                    UsuarioResponse.from(usuario)));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(401).build();
        }
    }
}
