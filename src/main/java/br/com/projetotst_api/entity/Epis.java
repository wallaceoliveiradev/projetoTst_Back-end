package br.com.projetotst_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "Epis")
public class Epis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do EPI.")
    @Size(max = 120, message = "O nome do EPI deve possuir no máximo 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe a categoria do EPI.")
    @Size(max = 40, message = "A categoria deve possuir no máximo 40 caracteres.")
    @Column(nullable = false, length = 40)
    private String categoria;

    @NotNull(message = "Informe a data de entrada.")
    @Column(nullable = false)
    private LocalDate dataEntrada;

    @NotNull(message = "Informe a quantidade.")
    @Min(value = 0, message = "A quantidade não pode ser negativa.")
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull(message = "Informe a validade.")
    @Column(nullable = false)
    private LocalDate validade;

    @NotBlank(message = "Informe o CA do EPI.")
    @Size(max = 80, message = "O CA deve possuir no máximo 80 caracteres.")
    @Column(nullable = false, length = 80)
    private String ca;

    protected Epis() {
    }

    public Epis(String nome, String categoria, LocalDate dataEntrada, Integer quantidade,
                LocalDate validade, String ca) {
        this.nome = nome;
        this.categoria = categoria;
        this.dataEntrada = dataEntrada;
        this.quantidade = quantidade;
        this.validade = validade;
        this.ca = ca;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public String getCa() {
        return ca;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }
}
