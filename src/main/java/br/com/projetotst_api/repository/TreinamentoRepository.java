package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Treinamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TreinamentoRepository extends JpaRepository<Treinamento, Long> {


}
