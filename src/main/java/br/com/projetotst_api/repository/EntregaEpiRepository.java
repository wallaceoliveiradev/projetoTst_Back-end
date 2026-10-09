package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.EntregaEpi;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EntregaEpiRepository extends JpaRepository<EntregaEpi, Long> {
    List<EntregaEpi> findAllByOrderByDataEntregaDescIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EntregaEpi e where e.id = :id")
    Optional<EntregaEpi> findByIdForUpdate(@Param("id") Long id);
}
