package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Epis;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisRepository extends JpaRepository<Epis, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Epis e where lower(e.nome) = lower(:nome) order by e.validade, e.dataEntrada, e.id")
    List<Epis> findAllByNomeForUpdate(@Param("nome") String nome);
}
