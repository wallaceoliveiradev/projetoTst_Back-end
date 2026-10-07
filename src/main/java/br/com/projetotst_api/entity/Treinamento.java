package br.com.projetotst_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Treinamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o Treinamento.")
    @Size(max = 80, message = "O nome deve possuir no máximo 80 caracteres.")
    @Column(nullable = false, length = 80)
    private String nome;

    @Column(length = 255)
    private String norma;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private Integer cargaHoraria;

    @Column(nullable = false)
    private Integer validade;

    @Column(nullable = false)
    private Integer participantes;

    @Column(length = 255)
    private String status;

    public Treinamento() {
    }

    public Treinamento (String nome, String norma, String descricao, Integer cargaHoraria, Integer validade, Integer participantes, String status){
        this.nome = nome;
        this.norma = norma;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
        this.validade = validade;
        this.participantes = participantes;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNorma() {
        return norma;
    }

    public void setNorma(String norma) {
        this.norma = norma;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Integer getValidade() {
        return validade;
    }

    public void setValidade(Integer validade) {
        this.validade = validade;
    }

    public Integer getParticipantes() {
        return participantes;
    }

    public void setParticipantes(Integer participantes) {
        this.participantes = participantes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
