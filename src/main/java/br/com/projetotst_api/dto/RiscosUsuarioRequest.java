package br.com.projetotst_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RiscosUsuarioRequest(
        @NotNull List<@NotNull @Valid RiscoUsuarioRequest> riscos
) {
}
