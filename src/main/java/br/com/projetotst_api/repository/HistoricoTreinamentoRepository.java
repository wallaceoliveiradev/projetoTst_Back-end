package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.HistoricoTreinamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoricoTreinamentoRepository extends JpaRepository<HistoricoTreinamento, Long> {
    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    List<HistoricoTreinamento> findAllByOrderByDataConclusaoDesc();

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    List<HistoricoTreinamento> findDistinctByColaboradorEmailIgnoreCaseOrderByDataConclusaoDesc(String email);

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    List<HistoricoTreinamento> findByTreinamentoIdAndColaboradorIdAndStatusNot(
            Long treinamentoId, Long colaboradorId, String status);

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    Optional<HistoricoTreinamento> findWithTreinamentoAndColaboradorById(Long id);
}
