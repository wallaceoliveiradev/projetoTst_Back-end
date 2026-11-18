package br.com.projetotst_api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record RelatorioDadosResponse(
        String tipo,
        String titulo,
        LocalDate inicio,
        LocalDate fim,
        Instant geradoEm,
        List<String> colunas,
        List<List<Object>> linhas) {
}
