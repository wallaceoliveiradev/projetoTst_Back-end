package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Inspecao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InspecaoRepository extends JpaRepository<Inspecao, Long> {

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    List<Inspecao> findAllByOrderByDataDescIdDesc();

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    List<Inspecao> findAllByUsuarioIdOrderByDataDescIdDesc(Long usuarioId);

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    Optional<Inspecao> findDetalhadaById(Long id);

    @EntityGraph(attributePaths = {"respostas", "usuario"})
    Optional<Inspecao> findByIdAndUsuarioId(Long id, Long usuarioId);
}
