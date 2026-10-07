package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Treinamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TreinamentoRepository extends JpaRepository<Treinamento, Long> {
    List<Treinamento> findDistinctByColaboradoresEmailIgnoreCase(String email);
}
