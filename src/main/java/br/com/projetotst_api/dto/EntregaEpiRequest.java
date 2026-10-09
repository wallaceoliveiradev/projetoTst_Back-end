package br.com.projetotst_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record EntregaEpiRequest(
        @NotNull Long funcionarioId,
        @NotNull LocalDate dataEntrega,
        @Size(max = 1000) String observacoes,
        @NotEmpty List<@Valid Item> itens) {

    public record Item(
            @jakarta.validation.constraints.NotBlank @Size(max = 120) String nome,
            @NotNull @jakarta.validation.constraints.Min(1) Integer quantidade) {
    }
}
