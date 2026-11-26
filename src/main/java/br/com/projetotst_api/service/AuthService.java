package br.com.projetotst_api.service;

import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.UsuarioRepository;
import br.com.projetotst_api.security.JwtService;
import br.com.projetotst_api.entity.NotificacaoAdmin;
import br.com.projetotst_api.repository.NotificacaoAdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final NotificacaoAdminRepository notificacaoRepository;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, PasswordEncoder passwordEncoder,
                       NotificacaoAdminRepository notificacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional(noRollbackFor = {CredenciaisInvalidasException.class, ContaInativaException.class,
            ContaBloqueadaException.class})
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseForUpdate(email).orElse(null);
        if (usuario == null) throw new CredenciaisInvalidasException();
        if (!"ATIVO".equalsIgnoreCase(usuario.getStatus())) throw new ContaInativaException();
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            int tentativas = usuario.getTentativasLoginFalhas() + 1;
            usuario.setTentativasLoginFalhas(tentativas);
            if (tentativas >= 5) {
                usuario.setStatus("INATIVO");
                usuario.setTentativasLoginFalhas(0);
                usuarioRepository.save(usuario);
                notificacaoRepository.save(new NotificacaoAdmin(usuario));
                throw new ContaBloqueadaException();
            }
            usuarioRepository.save(usuario);
            throw new CredenciaisInvalidasException();
        }
        usuario.setTentativasLoginFalhas(0);
        return usuarioRepository.save(usuario);
    }

    public String gerarToken(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElseThrow();
        var userDetails = org.springframework.security.core.userdetails.User.withUsername(usuario.getEmail())
                .password(usuario.getSenha()).roles(usuario.getPerfil()).build();
        return jwtService.generateToken(userDetails);
    }

    public long expiracaoEmSegundos() {
        return jwtService.getExpirationSeconds();
    }

    public static class CredenciaisInvalidasException extends RuntimeException {}
    public static class ContaInativaException extends RuntimeException {}
    public static class ContaBloqueadaException extends RuntimeException {}
}
