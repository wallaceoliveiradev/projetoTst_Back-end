package br.com.projetotst_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InvalidacaoTreinamentoRequest(
        @NotBlank(message = "Informe o motivo da reciclagem extraordinária.")
        @Size(max = 500, message = "O motivo deve possuir no máximo 500 caracteres.")
        String motivo
) {
}
