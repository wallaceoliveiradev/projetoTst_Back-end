package br.com.projetotst_api.config;

import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Profile("dev")
public class DevAdminInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevAdminInitializer.class);

    @Bean
    CommandLineRunner criarAdministradorInicial(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:admin@localhost}") String email,
            @Value("${app.admin.password:admin123}") String password,
            @Value("${app.admin.reset-password:false}") boolean resetPassword) {
        return args -> {
            var administrador = repository.findByEmailIgnoreCase(email);
            if (administrador.isEmpty()) {
                repository.save(new Usuario("Administrador", email,
                        passwordEncoder.encode(password), "ADMIN", "Administrador", "ATIVO"));
            } else if (resetPassword) {
                Usuario usuario = administrador.get();
                usuario.setSenha(passwordEncoder.encode(password));
                usuario.setStatus("ATIVO");
                repository.save(usuario);
                LOGGER.info("Senha da conta administrativa de desenvolvimento atualizada a partir da configuração.");
            }
        };
    }
}
