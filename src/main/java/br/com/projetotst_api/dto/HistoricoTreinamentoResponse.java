package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.HistoricoTreinamento;

import java.time.LocalDate;

public record HistoricoTreinamentoResponse(
        long id,
        long treinamentoId,
        String treinamentoNome,
        String norma,
        long usuarioId,
        String colaboradorNome,
        String colaboradorEmail,
        String cargo,
        LocalDate dataConclusao,
        LocalDate dataVencimento,
        Integer validadeAplicadaMeses,
        Integer diasAntecedenciaAlerta,
        Integer cargaHoraria,
        String instrutor,
        String conteudoProgramatico,
        String certificadoNome,
        String certificadoContentType,
        String status,
        String motivoInvalidacao,
        LocalDate dataInvalidacao
) {
    public static HistoricoTreinamentoResponse from(HistoricoTreinamento historico, String statusCalculado) {
        return new HistoricoTreinamentoResponse(
                historico.getId(),
                historico.getTreinamento().getId(),
                historico.getTreinamento().getNome(),
                historico.getTreinamento().getNorma(),
                historico.getColaborador().getId(),
                historico.getColaborador().getNome(),
                historico.getColaborador().getEmail(),
                historico.getColaborador().getCargo(),
                historico.getDataConclusao(),
                historico.getDataVencimento(),
                historico.getValidadeAplicadaMeses(),
                historico.getDiasAntecedenciaAlerta(),
                historico.getCargaHoraria(),
                historico.getInstrutor(),
                historico.getConteudoProgramatico(),
                historico.getCertificadoNome(),
                historico.getCertificadoContentType(),
                statusCalculado,
                historico.getMotivoInvalidacao(),
                historico.getDataInvalidacao());
    }
}
