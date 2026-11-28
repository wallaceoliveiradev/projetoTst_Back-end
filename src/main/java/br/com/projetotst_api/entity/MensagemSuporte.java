package br.com.projetotst_api.entity;

import br.com.projetotst_api.model.TipoSuporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem_suporte")
public class MensagemSuporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoSuporte tipo;

    @Column(nullable = false, length = 4000)
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    @Column(nullable = false)
    private boolean visualizada;

    protected MensagemSuporte() {}

    public MensagemSuporte(Usuario usuario, TipoSuporte tipo, String mensagem) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.mensagem = mensagem;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public TipoSuporte getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public boolean isVisualizada() { return visualizada; }
    public void setVisualizada(boolean visualizada) { this.visualizada = visualizada; }
}
