package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.model.StatusTreinamento;

public record TreinamentoResponse (
        long id,
        String nome,
        String norma,
        String descricao,
        Integer cargaHoraria,
        Integer validade,
        Integer participantes,
        StatusTreinamento  status
) {
    public static TreinamentoResponse from (Treinamento treinamento) {
        return new TreinamentoResponse(
                treinamento.getId(),
                treinamento.getNome(),
                treinamento.getNorma(),
                treinamento.getDescricao(),
                treinamento.getCargaHoraria(),
                treinamento.getValidade(),
                treinamento.getParticipantes(),
                StatusTreinamento.valueOf(treinamento.getStatus())

        );
    }

}
