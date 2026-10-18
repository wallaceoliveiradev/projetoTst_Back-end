package br.com.projetotst_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "historico_treinamento")
public class HistoricoTreinamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treinamento_id", nullable = false)
    private Treinamento treinamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario colaborador;

    @Column(nullable = false)
    private LocalDate dataConclusao;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Column(nullable = false)
    private Integer validadeAplicadaMeses;

    @Column(nullable = false)
    private Integer diasAntecedenciaAlerta;

    @Column(nullable = false)
    private Integer cargaHoraria;

    @Column(nullable = false, length = 120)
    private String instrutor;

    @Column(nullable = false, length = 4000)
    private String conteudoProgramatico;

    @Column(nullable = false, length = 255)
    private String certificadoNome;

    @Column(nullable = false, length = 100)
    private String certificadoContentType;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "arquivo_certificado_id", nullable = false, unique = true)
    private ArquivoCertificadoTreinamento arquivoCertificado;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 500)
    private String motivoInvalidacao;

    private LocalDate dataInvalidacao;

    protected HistoricoTreinamento() {
    }

    public HistoricoTreinamento(Treinamento treinamento, Usuario colaborador, LocalDate dataConclusao,
                                LocalDate dataVencimento, Integer validadeAplicadaMeses,
                                Integer diasAntecedenciaAlerta, Integer cargaHoraria, String instrutor,
                                String conteudoProgramatico, String certificadoNome,
                                String certificadoContentType, byte[] certificado) {
        this.treinamento = treinamento;
        this.colaborador = colaborador;
        this.dataConclusao = dataConclusao;
        this.dataVencimento = dataVencimento;
        this.validadeAplicadaMeses = validadeAplicadaMeses;
        this.diasAntecedenciaAlerta = diasAntecedenciaAlerta;
        this.cargaHoraria = cargaHoraria;
        this.instrutor = instrutor;
        this.conteudoProgramatico = conteudoProgramatico;
        this.certificadoNome = certificadoNome;
        this.certificadoContentType = certificadoContentType;
        this.arquivoCertificado = new ArquivoCertificadoTreinamento(certificado);
        this.status = StatusHistoricoTreinamento.VALIDO.name();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Treinamento getTreinamento() { return treinamento; }
    public Usuario getColaborador() { return colaborador; }
    public LocalDate getDataConclusao() { return dataConclusao; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public Integer getValidadeAplicadaMeses() { return validadeAplicadaMeses; }
    public Integer getDiasAntecedenciaAlerta() { return diasAntecedenciaAlerta; }
    public Integer getCargaHoraria() { return cargaHoraria; }
    public String getInstrutor() { return instrutor; }
    public String getConteudoProgramatico() { return conteudoProgramatico; }
    public String getCertificadoNome() { return certificadoNome; }
    public String getCertificadoContentType() { return certificadoContentType; }
    public byte[] getCertificado() { return arquivoCertificado.getConteudo(); }
    public String getStatus() { return status; }
    public String getMotivoInvalidacao() { return motivoInvalidacao; }
    public LocalDate getDataInvalidacao() { return dataInvalidacao; }

    public void setStatus(String status) { this.status = status; }
    public void invalidar(String motivo, LocalDate data) {
        this.status = StatusHistoricoTreinamento.INVALIDADO.name();
        this.motivoInvalidacao = motivo;
        this.dataInvalidacao = data;
    }
}
