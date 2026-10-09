package br.com.projetotst_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class EpisRequest {

    @NotBlank(message = "Informe o nome do EPI.")
    @Size(max = 120, message = "O nome do EPI deve possuir no máximo 120 caracteres.")
    private String nome;

    @NotBlank(message = "Informe a categoria do EPI.")
    @Size(max = 40, message = "A categoria deve possuir no máximo 40 caracteres.")
    private String categoria;

    @NotNull(message = "Informe a data de entrada.")
    private LocalDate dataEntrada;

    @NotNull(message = "Informe a quantidade.")
    @Min(value = 0, message = "A quantidade não pode ser negativa.")
    private Integer quantidade;

    @NotNull(message = "Informe a validade.")
    private LocalDate validade;

    @NotBlank(message = "Informe o CA do EPI.")
    @Size(max = 80, message = "O CA deve possuir no máximo 80 caracteres.")
    private String ca;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }
}
