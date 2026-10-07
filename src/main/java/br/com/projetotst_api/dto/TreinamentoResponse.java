package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Treinamento;
import br.com.projetotst_api.model.StatusTreinamento;

import java.util.List;

public record TreinamentoResponse (
        long id,
        String nome,
        String norma,
        String descricao,
        Integer cargaHoraria,
        Integer validade,
        Integer participantes,
        StatusTreinamento  status,
        List<Long> usuarioIds
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
                StatusTreinamento.valueOf(treinamento.getStatus()),
                treinamento.getColaboradores().stream().map(usuario -> usuario.getId()).toList()

        );
    }

}
