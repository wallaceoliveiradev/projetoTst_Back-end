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

import java.time.LocalDate;

@Entity
@Table(name = "resposta_inspecao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"inspecao_id", "item_id"}))
public class RespostaInspecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspecao_id", nullable = false)
    private Inspecao inspecao;

    @Column(name = "item_id", nullable = false, length = 80)
    private String itemId;

    @Column(nullable = false, length = 220)
    private String descricao;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 1000)
    private String observacao;

    @Column(length = 20)
    private String prioridade;

    @Column(name = "prazo_correcao")
    private LocalDate prazoCorrecao;

    @Column(name = "responsavel_correcao", length = 120)
    private String responsavelCorrecao;

    protected RespostaInspecao() {
    }

    public RespostaInspecao(String itemId, String descricao, String status, String observacao,
                            String prioridade, LocalDate prazoCorrecao, String responsavelCorrecao) {
        this.itemId = itemId;
        this.descricao = descricao;
        this.status = status;
        this.observacao = observacao;
        this.prioridade = prioridade;
        this.prazoCorrecao = prazoCorrecao;
        this.responsavelCorrecao = responsavelCorrecao;
    }

    public void setInspecao(Inspecao inspecao) { this.inspecao = inspecao; }

    public Long getId() { return id; }
    public String getItemId() { return itemId; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
    public String getObservacao() { return observacao; }
    public String getPrioridade() { return prioridade; }
    public LocalDate getPrazoCorrecao() { return prazoCorrecao; }
    public String getResponsavelCorrecao() { return responsavelCorrecao; }
}
