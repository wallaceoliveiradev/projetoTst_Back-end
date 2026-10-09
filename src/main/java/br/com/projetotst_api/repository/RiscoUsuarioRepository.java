package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.RiscoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiscoUsuarioRepository extends JpaRepository<RiscoUsuario, Long> {
    List<RiscoUsuario> findByUsuario_IdOrderByIdAsc(Long usuarioId);
}
