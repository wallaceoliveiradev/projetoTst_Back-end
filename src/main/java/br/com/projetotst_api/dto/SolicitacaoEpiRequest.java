package br.com.projetotst_api.dto;

import br.com.projetotst_api.model.TipoSolicitacaoEpi;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitacaoEpiRequest(
        @NotNull TipoSolicitacaoEpi tipo,
        @NotBlank @Size(max = 120) String epi,
        @NotNull @Min(1) Integer quantidade,
        @NotBlank @Size(max = 500) String motivo) {
}
