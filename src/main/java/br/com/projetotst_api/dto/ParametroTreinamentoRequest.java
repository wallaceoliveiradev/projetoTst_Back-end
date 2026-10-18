package br.com.projetotst_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ParametroTreinamentoRequest(
        @Size(max = 100, message = "O cargo deve possuir no máximo 100 caracteres.")
        String cargo,
        @Positive(message = "A validade deve ser maior que zero.")
        @Max(value = 120, message = "A validade máxima é de 120 meses.")
        Integer validadeMeses,
        @Min(value = 0, message = "A antecedência não pode ser negativa.")
        @Max(value = 365, message = "A antecedência máxima é de 365 dias.")
        Integer diasAntecedenciaAlerta
) {
}
