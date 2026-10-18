package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.EntregaEpi;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface EntregaEpiRepository extends JpaRepository<EntregaEpi, Long> {
    interface ContagemPorNome {
        String getRotulo();
        Long getQuantidade();
    }

    List<EntregaEpi> findAllByOrderByDataEntregaDescIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EntregaEpi e where e.id = :id")
    Optional<EntregaEpi> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select coalesce(sum(i.quantidade), 0)
            from EntregaEpi e join e.itens i
            where e.dataEntrega between :inicio and :fim
            """)
    long somarUnidadesEntregues(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
            select i.epi.nome as rotulo, sum(i.quantidade) as quantidade
            from EntregaEpi e join e.itens i
            where e.dataEntrega between :inicio and :fim
            group by i.epi.nome
            order by i.epi.nome
            """)
    List<ContagemPorNome> somarUnidadesPorEpi(
            @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
