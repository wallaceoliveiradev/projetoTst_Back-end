package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.Usuario;

public record UsuarioResponse(Long id, String nome, String email, String perfil, String cargo, String status) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getPerfil(), usuario.getCargo(), usuario.getStatus());
    }
}
