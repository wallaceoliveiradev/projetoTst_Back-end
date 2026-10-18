package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Inspecao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InspecaoRepository extends JpaRepository<Inspecao, Long> {
    interface ContagemPorStatus {
        String getRotulo();
        Long getQuantidade();
    }

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    List<Inspecao> findAllByOrderByDataDescIdDesc();

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    List<Inspecao> findAllByUsuarioIdOrderByDataDescIdDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    Optional<Inspecao> findDetalhadaById(Long id);

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    Optional<Inspecao> findByIdAndUsuarioId(Long id, Long usuarioId);

    long countByDataBetween(LocalDate inicio, LocalDate fim);

    @Query("""
            select r.status as rotulo, count(r) as quantidade
            from Inspecao i join i.respostas r
            where i.data between :inicio and :fim
            group by r.status
            order by r.status
            """)
    List<ContagemPorStatus> contarRespostasPorStatus(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
