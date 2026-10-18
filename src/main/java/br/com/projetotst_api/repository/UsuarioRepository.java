package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByPerfilIgnoreCaseAndStatusIgnoreCase(String perfil, String status);
}
