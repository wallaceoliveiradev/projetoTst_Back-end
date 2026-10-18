package br.com.projetotst_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "parametro_treinamento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"treinamento_id", "cargo"}))
public class ParametroTreinamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treinamento_id", nullable = false)
    private Treinamento treinamento;

    @Column(length = 100)
    private String cargo;

    @Column(nullable = false)
    private Integer validadeMeses;

    @Column(nullable = false)
    private Integer diasAntecedenciaAlerta = 60;

    protected ParametroTreinamento() {
    }

    public ParametroTreinamento(Treinamento treinamento, String cargo, Integer validadeMeses,
                                Integer diasAntecedenciaAlerta) {
        this.treinamento = treinamento;
        this.cargo = cargo;
        this.validadeMeses = validadeMeses;
        this.diasAntecedenciaAlerta = diasAntecedenciaAlerta;
    }

    public Long getId() { return id; }
    public Treinamento getTreinamento() { return treinamento; }
    public String getCargo() { return cargo; }
    public Integer getValidadeMeses() { return validadeMeses; }
    public Integer getDiasAntecedenciaAlerta() { return diasAntecedenciaAlerta; }

    public void setTreinamento(Treinamento treinamento) { this.treinamento = treinamento; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setValidadeMeses(Integer validadeMeses) { this.validadeMeses = validadeMeses; }
    public void setDiasAntecedenciaAlerta(Integer diasAntecedenciaAlerta) {
        this.diasAntecedenciaAlerta = diasAntecedenciaAlerta;
    }
}
