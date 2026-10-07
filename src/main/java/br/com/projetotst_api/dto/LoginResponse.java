package br.com.projetotst_api.dto;

public record LoginResponse(String token, long expiresIn, UsuarioResponse usuario) {
}
