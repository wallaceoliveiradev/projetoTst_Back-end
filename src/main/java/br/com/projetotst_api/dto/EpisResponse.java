package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Epis;

import java.time.LocalDate;

public record EpisResponse(
        Long id,
        String nome,
        String categoria,
        LocalDate dataEntrada,
        Integer quantidade,
        LocalDate validade,
        String ca
) {

    public static EpisResponse from(Epis epis) {
        return new EpisResponse(
                epis.getId(),
                epis.getNome(),
                epis.getCategoria(),
                epis.getDataEntrada(),
                epis.getQuantidade(),
                epis.getValidade(),
                epis.getCa()
        );
    }
}
