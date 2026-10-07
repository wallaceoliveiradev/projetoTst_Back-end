package br.com.projetotst_api.controller;

import br.com.projetotst_api.dto.UsuarioMeRequest;
import br.com.projetotst_api.dto.UsuarioRequest;
import br.com.projetotst_api.dto.UsuarioResponse;
import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public UsuarioResponse me(Authentication authentication) {
        return UsuarioResponse.from(usuarioAutenticado(authentication));
    }

    @PatchMapping("/me")
    public UsuarioResponse atualizarMe(Authentication authentication,
                                       @Valid @RequestBody UsuarioMeRequest request) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (request.nome() != null && !request.nome().isBlank()) usuario.setNome(request.nome());
        if (request.email() != null && !request.email().isBlank()) usuario.setEmail(request.email());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        return UsuarioResponse.from(repository.save(usuario));
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario usuario = new Usuario(request.nome(), request.email(),
                passwordEncoder.encode(request.senha()),
                perfilOuPadrao(request.perfil()), request.cargo(), statusOuPadrao(request.status()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(repository.save(usuario)));
    }

    @PutMapping("/{id}")
    public UsuarioResponse editar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = repository.findById(id).orElseThrow();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(perfilOuPadrao(request.perfil()));
        usuario.setCargo(request.cargo());
        usuario.setStatus(statusOuPadrao(request.status()));
        return UsuarioResponse.from(repository.save(usuario));
    }

    private Usuario usuarioAutenticado(Authentication authentication) {
        return repository.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
    }

    private String perfilOuPadrao(String perfil) {
        String valor = perfil == null || perfil.isBlank() ? "COLABORADOR" : perfil.toUpperCase();
        if (!valor.equals("ADMIN") && !valor.equals("COLABORADOR")) {
            throw new IllegalArgumentException("Perfil deve ser ADMIN ou COLABORADOR");
        }
        return valor;
    }

    private String statusOuPadrao(String status) {
        return status == null || status.isBlank() ? "ATIVO" : status.toUpperCase();
    }
}
