package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Risco;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;

public record RiscoResponse(
        Long id,
        String setor,
        String risco,
        NivelRisco nivel,
        SituacaoRisco situacao,
        String icone
) {
    public static RiscoResponse from(Risco risco) {
        return new RiscoResponse(
                risco.getId(),
                risco.getSetor(),
                risco.getRisco(),
                risco.getNivel(),
                risco.getSituacao(),
                risco.getIcone());
    }
}
