package br.com.projetotst_api.dto;

import br.com.projetotst_api.entity.NotificacaoAdmin;
import java.time.LocalDateTime;

public record NotificacaoAdminResponse(Long id, Long usuarioId, String emailUsuario,
                                      String nomeUsuario, LocalDateTime criadaEm, boolean lida) {
    public static NotificacaoAdminResponse from(NotificacaoAdmin notificacao) {
        return new NotificacaoAdminResponse(notificacao.getId(), notificacao.getUsuarioId(),
                notificacao.getEmailUsuario(), notificacao.getNomeUsuario(), notificacao.getCriadaEm(), notificacao.isLida());
    }
}
