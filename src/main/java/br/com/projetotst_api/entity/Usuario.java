package br.com.projetotst_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 30)
    private String perfil;

    @Column(length = 100)
    private String cargo;

    @Column(nullable = false, length = 30)
    private String status;

    protected Usuario() {
    }

    public Usuario(String nome, String email, String senha, String perfil) {
        this(nome, email, senha, perfil, null, "ATIVO");
    }

    public Usuario(String nome, String email, String senha, String perfil, String cargo, String status) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.cargo = cargo;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getPerfil() { return perfil; }
    public String getCargo() { return cargo; }
    public String getStatus() { return status; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return id != null && Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
