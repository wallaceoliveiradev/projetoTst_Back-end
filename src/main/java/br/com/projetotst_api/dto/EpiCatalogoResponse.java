package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Epis;

public record EpiCatalogoResponse(String nome, String categoria, String ca) {
    public static EpiCatalogoResponse from(Epis epi) {
        return new EpiCatalogoResponse(epi.getNome(), epi.getCategoria(), epi.getCa());
    }
}
