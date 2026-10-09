package br.com.projetotst_api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EntregaEpi")
public class EntregaEpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate dataEntrega;

    @Column(length = 1000)
    private String observacoes;

    @OneToMany(mappedBy = "entrega", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntregaEpiItem> itens = new ArrayList<>();

    protected EntregaEpi() {
    }

    public EntregaEpi(Usuario usuario, LocalDate dataEntrega, String observacoes) {
        this.usuario = usuario;
        this.dataEntrega = dataEntrega;
        this.observacoes = observacoes;
    }

    public void adicionarItem(EntregaEpiItem item) {
        itens.add(item);
        item.setEntrega(this);
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getDataEntrega() { return dataEntrega; }
    public String getObservacoes() { return observacoes; }
    public List<EntregaEpiItem> getItens() { return itens; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
