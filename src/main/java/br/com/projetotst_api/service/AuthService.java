package br.com.projetotst_api.service;

import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.UsuarioRepository;
import br.com.projetotst_api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public Usuario autenticar(String email, String senha) {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, senha));
        return usuarioRepository.findByEmailIgnoreCase(email).orElseThrow();
    }

    public String gerarToken(String email, String senha) {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, senha));
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElseThrow();
        var userDetails = org.springframework.security.core.userdetails.User.withUsername(usuario.getEmail())
                .password(usuario.getSenha()).roles(usuario.getPerfil()).build();
        return jwtService.generateToken(userDetails);
    }

    public long expiracaoEmSegundos() {
        return jwtService.getExpirationSeconds();
    }
}
