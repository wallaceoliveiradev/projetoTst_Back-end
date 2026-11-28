package br.com.projetotst_api.entity;

import br.com.projetotst_api.model.TipoSolicitacaoEpi;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_epi")
public class SolicitacaoEpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSolicitacaoEpi tipo;

    @Column(nullable = false, length = 120)
    private String epi;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @Column(nullable = false)
    private boolean visualizada;

    protected SolicitacaoEpi() {}

    public SolicitacaoEpi(Usuario usuario, TipoSolicitacaoEpi tipo, String epi,
                          Integer quantidade, String motivo, LocalDateTime criadaEm) {
        this.usuario = usuario;
        this.tipo = tipo;
        this.epi = epi;
        this.quantidade = quantidade;
        this.motivo = motivo;
        this.criadaEm = criadaEm;
        this.visualizada = false;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public TipoSolicitacaoEpi getTipo() { return tipo; }
    public String getEpi() { return epi; }
    public Integer getQuantidade() { return quantidade; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public boolean isVisualizada() { return visualizada; }
    public void setVisualizada(boolean visualizada) { this.visualizada = visualizada; }
}
