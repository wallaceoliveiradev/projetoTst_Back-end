package br.com.projetotst_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inspecao")
public class Inspecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 20)
    private String tipoId;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(name = "local_inspecao", length = 160)
    private String local;

    @Column(length = 120)
    private String setor;

    @Column(length = 40)
    private String numeroOnibus;

    @Column(length = 20)
    private String placa;

    @Column(nullable = false, length = 120)
    private String responsavel;

    @Column(length = 2000)
    private String observacoesGerais;

    @OneToMany(mappedBy = "inspecao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespostaInspecao> respostas = new ArrayList<>();

    protected Inspecao() {
    }

    public Inspecao(Usuario usuario, LocalDate data, String tipoId, String tipo, String local,
                    String setor, String numeroOnibus, String placa, String responsavel,
                    String observacoesGerais) {
        this.usuario = usuario;
        this.data = data;
        this.tipoId = tipoId;
        this.tipo = tipo;
        this.local = local;
        this.setor = setor;
        this.numeroOnibus = numeroOnibus;
        this.placa = placa;
        this.responsavel = responsavel;
        this.observacoesGerais = observacoesGerais;
    }

    public void adicionarResposta(RespostaInspecao resposta) {
        respostas.add(resposta);
        resposta.setInspecao(this);
    }

    public void atualizar(LocalDate data, String tipoId, String tipo, String local, String setor,
                          String numeroOnibus, String placa, String responsavel,
                          String observacoesGerais) {
        this.data = data;
        this.tipoId = tipoId;
        this.tipo = tipo;
        this.local = local;
        this.setor = setor;
        this.numeroOnibus = numeroOnibus;
        this.placa = placa;
        this.responsavel = responsavel;
        this.observacoesGerais = observacoesGerais;
        respostas.clear();
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getData() { return data; }
    public String getTipoId() { return tipoId; }
    public String getTipo() { return tipo; }
    public String getLocal() { return local; }
    public String getSetor() { return setor; }
    public String getNumeroOnibus() { return numeroOnibus; }
    public String getPlaca() { return placa; }
    public String getResponsavel() { return responsavel; }
    public String getObservacoesGerais() { return observacoesGerais; }
    public List<RespostaInspecao> getRespostas() { return respostas; }
}
