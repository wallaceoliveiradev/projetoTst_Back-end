package br.com.projetotst_api.dto;

import br.com.projetotst_api.model.TipoSuporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MensagemSuporteRequest(
        @NotNull TipoSuporte tipo,
        @NotBlank @Size(max = 4000) String mensagem) {
}
