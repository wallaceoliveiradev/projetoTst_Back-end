package br.com.projetotst_api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record RelatorioResumoResponse(
        LocalDate inicio,
        LocalDate fim,
        LocalDate dataReferenciaAtual,
        Instant geradoEm,
        long colaboradoresAtivosAtuais,
        long conclusoesNoPeriodo,
        long unidadesEpiEntreguesNoPeriodo,
        long riscosAtivosAtuais,
        List<Quantidade> treinamentosPorTipo,
        List<Quantidade> episPorTipo,
        List<Quantidade> riscosPorNivel,
        List<QuantidadeMensal> treinamentosPorMes) {

    public record Quantidade(String rotulo, long quantidade) {
    }

    public record QuantidadeMensal(String anoMes, long quantidade) {
    }
}
