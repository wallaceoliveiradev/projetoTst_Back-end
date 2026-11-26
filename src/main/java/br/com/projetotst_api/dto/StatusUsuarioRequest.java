package br.com.projetotst_api.dto;

import jakarta.validation.constraints.NotBlank;

public record StatusUsuarioRequest(@NotBlank String status) {}
