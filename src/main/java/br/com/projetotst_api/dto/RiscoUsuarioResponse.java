package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.RiscoUsuario;
import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;

import java.time.LocalDate;

public record RiscoUsuarioResponse(
        Long riscoId,
        String setor,
        String risco,
        NivelRisco nivel,
        SituacaoRisco situacao,
        LocalDate validade,
        boolean validadeValida,
        boolean validado
) {
    public static RiscoUsuarioResponse from(RiscoUsuario associacao) {
        return new RiscoUsuarioResponse(
                associacao.getRisco().getId(),
                associacao.getRisco().getSetor(),
                associacao.getRisco().getRisco(),
                associacao.getRisco().getNivel(),
                associacao.getRisco().getSituacao(),
                associacao.getValidade(),
                !associacao.getValidade().isBefore(LocalDate.now()),
                associacao.isValidado());
    }
}
