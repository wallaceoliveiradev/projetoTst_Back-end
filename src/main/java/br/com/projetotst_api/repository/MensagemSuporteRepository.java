package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.MensagemSuporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemSuporteRepository extends JpaRepository<MensagemSuporte, Long> {
    List<MensagemSuporte> findAllByOrderByCriadaEmDesc();
}
