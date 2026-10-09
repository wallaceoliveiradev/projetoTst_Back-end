package br.com.projetotst_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RiscoUsuarioRequest(
        @NotNull @Positive Long riscoId,
        @NotNull LocalDate validade,
        boolean validado
) {
}
