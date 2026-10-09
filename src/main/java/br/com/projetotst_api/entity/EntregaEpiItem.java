package br.com.projetotst_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "EntregaEpiItem")
public class EntregaEpiItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entrega_id", nullable = false)
    private EntregaEpi entrega;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "epi_id", nullable = false)
    private Epis epi;

    @Column(nullable = false)
    private Integer quantidade;

    protected EntregaEpiItem() {
    }

    public EntregaEpiItem(Epis epi, Integer quantidade) {
        this.epi = epi;
        this.quantidade = quantidade;
    }

    public Long getId() { return id; }
    public EntregaEpi getEntrega() { return entrega; }
    public Epis getEpi() { return epi; }
    public Integer getQuantidade() { return quantidade; }
    public void setEntrega(EntregaEpi entrega) { this.entrega = entrega; }
}
