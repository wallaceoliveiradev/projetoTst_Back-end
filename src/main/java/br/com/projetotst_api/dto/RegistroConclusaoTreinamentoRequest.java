package br.com.projetotst_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroConclusaoTreinamentoRequest(
        @NotNull(message = "Selecione o treinamento.")
        Long treinamentoId,
        @NotNull(message = "Selecione o colaborador.")
        Long usuarioId,
        @NotNull(message = "Informe a data de conclusão.")
        LocalDate dataConclusao,
        @NotNull(message = "Informe a carga horária.")
        @Positive(message = "A carga horária deve ser maior que zero.")
        Integer cargaHoraria,
        @NotBlank(message = "Informe o nome do instrutor.")
        @Size(max = 120, message = "O nome do instrutor deve possuir no máximo 120 caracteres.")
        String instrutor,
        @NotBlank(message = "Informe o conteúdo programático.")
        @Size(max = 4000, message = "O conteúdo programático deve possuir no máximo 4000 caracteres.")
        String conteudoProgramatico
) {
}
