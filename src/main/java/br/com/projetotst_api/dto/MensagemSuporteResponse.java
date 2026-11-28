package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.MensagemSuporte;
import br.com.projetotst_api.model.TipoSuporte;

import java.time.LocalDateTime;

public record MensagemSuporteResponse(
        Long id,
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario,
        TipoSuporte tipo,
        String mensagem,
        LocalDateTime criadaEm,
        boolean visualizada) {

    public static MensagemSuporteResponse from(MensagemSuporte mensagem) {
        var usuario = mensagem.getUsuario();
        return new MensagemSuporteResponse(
                mensagem.getId(),
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                mensagem.getTipo(),
                mensagem.getMensagem(),
                mensagem.getCriadaEm(),
                mensagem.isVisualizada());
    }
}
