package br.com.projetotst_api.config;

import br.com.projetotst_api.entity.Usuario;
import br.com.projetotst_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevAdminInitializer {

    @Bean
    CommandLineRunner criarAdministradorInicial(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:admin@localhost}") String email,
            @Value("${app.admin.password:admin123}") String password) {
        return args -> {
            if (!repository.existsByEmailIgnoreCase(email)) {
                repository.save(new Usuario("Administrador", email,
                        passwordEncoder.encode(password), "ADMIN", "Administrador", "ATIVO"));
            }
        };
    }
}
