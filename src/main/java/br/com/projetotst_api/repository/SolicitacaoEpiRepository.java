package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.SolicitacaoEpi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoEpiRepository extends JpaRepository<SolicitacaoEpi, Long> {
    List<SolicitacaoEpi> findAllByOrderByCriadaEmDescIdDesc();
}
