package br.com.projetotst_api.dto;

import java.time.Instant;
import java.time.LocalDate;

public record DashboardResponse(
        LocalDate dataReferencia,
        Instant geradoEm,
        int diasAlertaEpi,
        long treinamentosVencidos,
        double percentualTreinamentosVencidos,
        long episProximosVencimento,
        double percentualEpisProximosVencimento,
        long colaboradoresCertificados,
        double percentualColaboradoresCertificados,
        long riscosAltosAtivos,
        double percentualRiscosAltosAtivos) {
}
