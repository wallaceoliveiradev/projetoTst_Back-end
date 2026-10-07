package br.com.projetotst_api.dto;

import br.com.projetotst_api.model.StatusTreinamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

public class TreinamentoRequest {

@NotBlank(message = "O nome é obrigatório.")
private String nome;

@NotBlank(message = "Norma obrigatória")
private String norma;

@NotBlank(message = "Informe a descrição")
private String descricao;

@NotNull(message = "A carga horária é obrigatória.")
@Positive(message = "A carga horária deve ser maior que zero.")
private Integer cargaHoraria;

@NotNull(message = "A validade é obrigatória.")
@Positive(message = "A validade deve ser maior que zero.")
private Integer validade;

@NotNull(message = "O número de participantes é obrigatório.")
@PositiveOrZero(message = "O número de participantes não pode ser negativo.")
private Integer participantes;

@NotNull(message = "O status é obrigatório.")
private StatusTreinamento status;

private List<Long> usuarioIds = new ArrayList<>();

// Getters e Setters
public String getNome() {
    return nome;
}

public void setNome(String nome) {
    this.nome = nome;
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

public StatusTreinamento getStatus() {
    return status;
}

public void setStatus(StatusTreinamento status) {
    this.status = status;
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

        public List<Long> getUsuarioIds() {
            return usuarioIds;
        }

        public void setUsuarioIds(List<Long> usuarioIds) {
            this.usuarioIds = usuarioIds == null ? new ArrayList<>() : usuarioIds;
        }
    }