package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.SolicitacaoEpi;
import br.com.projetotst_api.model.TipoSolicitacaoEpi;

import java.time.LocalDateTime;

public record SolicitacaoEpiResponse(
        Long id,
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario,
        TipoSolicitacaoEpi tipo,
        String epi,
        Integer quantidade,
        String motivo,
        LocalDateTime criadaEm,
        boolean visualizada) {

    public static SolicitacaoEpiResponse from(SolicitacaoEpi solicitacao) {
        var usuario = solicitacao.getUsuario();
        return new SolicitacaoEpiResponse(solicitacao.getId(), usuario.getId(),
                usuario.getNome(), usuario.getEmail(), solicitacao.getTipo(),
                solicitacao.getEpi(), solicitacao.getQuantidade(), solicitacao.getMotivo(),
                solicitacao.getCriadaEm(), solicitacao.isVisualizada());
    }
}
