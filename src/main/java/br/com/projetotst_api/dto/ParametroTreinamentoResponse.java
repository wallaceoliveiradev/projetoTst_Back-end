package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.ParametroTreinamento;

public record ParametroTreinamentoResponse(
        long id,
        long treinamentoId,
        String treinamentoNome,
        String norma,
        String cargo,
        Integer validadeMeses,
        Integer diasAntecedenciaAlerta
) {
    public static ParametroTreinamentoResponse from(ParametroTreinamento parametro) {
        return new ParametroTreinamentoResponse(
                parametro.getId(),
                parametro.getTreinamento().getId(),
                parametro.getTreinamento().getNome(),
                parametro.getTreinamento().getNorma(),
                parametro.getCargo(),
                parametro.getValidadeMeses(),
                parametro.getDiasAntecedenciaAlerta());
    }
}
