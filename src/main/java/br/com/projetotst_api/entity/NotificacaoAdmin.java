package br.com.projetotst_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao_admin")
public class NotificacaoAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 180)
    private String emailUsuario;

    @Column(nullable = false, length = 120)
    private String nomeUsuario;

    @Column(nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    @Column(nullable = false)
    private boolean lida = false;

    protected NotificacaoAdmin() {}

    public NotificacaoAdmin(Usuario usuario) {
        this.usuarioId = usuario.getId();
        this.emailUsuario = usuario.getEmail();
        this.nomeUsuario = usuario.getNome();
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getEmailUsuario() { return emailUsuario; }
    public String getNomeUsuario() { return nomeUsuario; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }
}
