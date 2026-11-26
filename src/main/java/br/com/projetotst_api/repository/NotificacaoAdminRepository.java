package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.NotificacaoAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoAdminRepository extends JpaRepository<NotificacaoAdmin, Long> {
    List<NotificacaoAdmin> findAllByOrderByCriadaEmDesc();
}
