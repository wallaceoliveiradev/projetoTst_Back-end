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
@Table(name = "usuario_risco", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "risco_id"})
})
public class RiscoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "risco_id", nullable = false)
    private Risco risco;

    @Column(nullable = false)
    private LocalDate validade;

    @Column(nullable = false)
    private boolean validado;

    protected RiscoUsuario() {
    }

    public RiscoUsuario(Usuario usuario, Risco risco, LocalDate validade, boolean validado) {
        this.usuario = usuario;
        this.risco = risco;
        this.validade = validade;
        this.validado = validado;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Risco getRisco() {
        return risco;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public boolean isValidado() {
        return validado;
    }
}
