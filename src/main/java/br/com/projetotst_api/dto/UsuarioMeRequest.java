package br.com.projetotst_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioMeRequest(
        String nome,
        @Email String email,
        @Size(min = 6) String senha
) {
}
