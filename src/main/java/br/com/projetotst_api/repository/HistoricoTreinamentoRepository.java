package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.HistoricoTreinamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoricoTreinamentoRepository extends JpaRepository<HistoricoTreinamento, Long> {
    interface ContagemPorRotulo {
        String getRotulo();
        Long getQuantidade();
    }

    interface ContagemPorStatus {
        String getRotulo();
        String getStatus();
        Long getQuantidade();
    }

    interface ContagemMensal {
        Integer getAno();
        Integer getMes();
        Long getQuantidade();
    }

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    List<HistoricoTreinamento> findAllByOrderByDataConclusaoDesc();

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    List<HistoricoTreinamento> findDistinctByColaboradorEmailIgnoreCaseOrderByDataConclusaoDesc(String email);

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    List<HistoricoTreinamento> findByTreinamentoIdAndColaboradorIdAndStatusNot(
            Long treinamentoId, Long colaboradorId, String status);

    @EntityGraph(attributePaths = {"treinamento", "colaborador"})
    Optional<HistoricoTreinamento> findWithTreinamentoAndColaboradorById(Long id);

    @Query("""
            select count(h) from HistoricoTreinamento h
            where h.status = 'VALIDO'
              and h.dataVencimento < :hoje
              and upper(h.colaborador.perfil) = 'COLABORADOR'
              and upper(h.colaborador.status) = 'ATIVO'
            """)
    long contarVencidos(@Param("hoje") LocalDate hoje);

    @Query("""
            select count(h) from HistoricoTreinamento h
            where h.status in ('VALIDO', 'INVALIDADO')
              and upper(h.colaborador.perfil) = 'COLABORADOR'
              and upper(h.colaborador.status) = 'ATIVO'
            """)
    long contarCiclosAtuais();

    @Query("""
            select count(distinct h.colaborador.id)
            from HistoricoTreinamento h
            where h.status = 'VALIDO'
              and h.dataConclusao <= :hoje
              and h.dataVencimento >= :hoje
              and upper(h.colaborador.perfil) = 'COLABORADOR'
              and upper(h.colaborador.status) = 'ATIVO'
            """)
    long contarColaboradoresCertificados(@Param("hoje") LocalDate hoje);

    @Query("""
            select h.treinamento.nome as rotulo, count(h) as quantidade
            from HistoricoTreinamento h
            where h.dataConclusao between :inicio and :fim
            group by h.treinamento.id, h.treinamento.nome
            order by h.treinamento.nome
            """)
    List<ContagemPorRotulo> contarConclusoesPorTreinamento(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
            select h.treinamento.nome as rotulo, h.status as status, count(h) as quantidade
            from HistoricoTreinamento h
            where h.dataConclusao between :inicio and :fim
            group by h.treinamento.id, h.treinamento.nome, h.status
            order by h.treinamento.nome, h.status
            """)
    List<ContagemPorStatus> contarConclusoesPorTreinamentoEStatus(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
            select year(h.dataConclusao) as ano, month(h.dataConclusao) as mes, count(h) as quantidade
            from HistoricoTreinamento h
            where h.dataConclusao between :inicio and :fim
            group by year(h.dataConclusao), month(h.dataConclusao)
            order by year(h.dataConclusao), month(h.dataConclusao)
            """)
    List<ContagemMensal> contarConclusoesPorMes(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    long countByDataConclusaoBetween(LocalDate inicio, LocalDate fim);

    long countByDataConclusaoBetweenAndStatus(
            LocalDate inicio, LocalDate fim, String status);
}
