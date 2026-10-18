package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Epis;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface EpisRepository extends JpaRepository<Epis, Long> {
    interface ContagemPorNome {
        String getRotulo();
        Long getQuantidade();
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Epis e where lower(e.nome) = lower(:nome) order by e.validade, e.dataEntrada, e.id")
    List<Epis> findAllByNomeForUpdate(@Param("nome") String nome);

    @Query("""
            select coalesce(sum(e.quantidade), 0) from Epis e
            where e.quantidade > 0 and e.validade between :hoje and :limite
            """)
    long somarProximosDoVencimento(
            @Param("hoje") LocalDate hoje, @Param("limite") LocalDate limite);

    @Query("select coalesce(sum(e.quantidade), 0) from Epis e where e.quantidade > 0")
    long somarSaldoEstoque();
}
