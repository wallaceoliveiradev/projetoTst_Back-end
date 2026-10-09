package br.com.projetotst_api.dto;

import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RiscoRequest(
        @NotBlank @Size(max = 120) String setor,
        @NotBlank @Size(max = 180) String risco,
        @NotNull NivelRisco nivel,
        @NotNull SituacaoRisco situacao,
        @Size(max = 64) String icone
) {
}
