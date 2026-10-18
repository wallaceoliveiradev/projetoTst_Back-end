package br.com.projetotst_api.repository;

import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiscoRepository extends JpaRepository<Risco, Long> {
    interface ContagemPorNivel {
        NivelRisco getRotulo();
        Long getQuantidade();
    }

    long countBySituacao(SituacaoRisco situacao);

    long countByNivelAndSituacao(NivelRisco nivel, SituacaoRisco situacao);

    @Query("""
            select r.nivel as rotulo, count(r) as quantidade
            from Risco r
            where r.situacao = :situacao
            group by r.nivel
            """)
    java.util.List<ContagemPorNivel> contarPorNivelESituacao(
            @Param("situacao") SituacaoRisco situacao);
}
