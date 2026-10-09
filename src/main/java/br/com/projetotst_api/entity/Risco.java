package br.com.projetotst_api.entity;

import br.com.projetotst_api.model.NivelRisco;
import br.com.projetotst_api.model.SituacaoRisco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "risco")
public class Risco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String setor;

    @NotBlank
    @Size(max = 180)
    @Column(nullable = false, length = 180)
    private String risco;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NivelRisco nivel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SituacaoRisco situacao;

    @Size(max = 64)
    @Column(length = 64)
    private String icone;

    protected Risco() {
    }

    public Risco(String setor, String risco, NivelRisco nivel, SituacaoRisco situacao, String icone) {
        this.setor = setor;
        this.risco = risco;
        this.nivel = nivel;
        this.situacao = situacao;
        this.icone = icone;
    }

    public Long getId() {
        return id;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public NivelRisco getNivel() {
        return nivel;
    }

    public void setNivel(NivelRisco nivel) {
        this.nivel = nivel;
    }

    public SituacaoRisco getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoRisco situacao) {
        this.situacao = situacao;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }
}
