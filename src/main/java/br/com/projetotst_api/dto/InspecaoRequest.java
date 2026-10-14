package br.com.projetotst_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record InspecaoRequest(
        @NotNull LocalDate data,
        @NotBlank @Pattern(regexp = "onibus|garagem|oficina|epis|emergencia") String tipoId,
        @NotBlank @Size(max = 60) String tipo,
        @Size(max = 160) String local,
        @Size(max = 120) String setor,
        @Size(max = 40) String numeroOnibus,
        @Size(max = 20) String placa,
        @NotBlank @Size(max = 120) String responsavel,
        @Size(max = 2000) String observacoesGerais,
        @NotEmpty List<@Valid Resposta> respostas) {

    public record Resposta(
            @NotBlank @Size(max = 80) String itemId,
            @NotBlank @Size(max = 220) String descricao,
            @NotBlank @Pattern(regexp = "conforme|naoConforme|pendente") String status,
            @Size(max = 1000) String observacao,
            @Pattern(regexp = "Baixa|Média|Alta", message = "Prioridade inválida.") String prioridade,
            LocalDate prazoCorrecao,
            @Size(max = 120) String responsavelCorrecao) {
    }
}
