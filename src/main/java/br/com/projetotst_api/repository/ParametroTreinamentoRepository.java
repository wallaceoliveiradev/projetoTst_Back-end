package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.ParametroTreinamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParametroTreinamentoRepository extends JpaRepository<ParametroTreinamento, Long> {
    List<ParametroTreinamento> findByTreinamentoIdOrderByCargoAsc(Long treinamentoId);
    List<ParametroTreinamento> findAllByOrderByTreinamentoNomeAscCargoAsc();
    Optional<ParametroTreinamento> findByTreinamentoIdAndCargoIgnoreCase(Long treinamentoId, String cargo);
    Optional<ParametroTreinamento> findByTreinamentoIdAndCargoIsNull(Long treinamentoId);
    Optional<ParametroTreinamento> findByIdAndTreinamentoId(Long id, Long treinamentoId);
}
