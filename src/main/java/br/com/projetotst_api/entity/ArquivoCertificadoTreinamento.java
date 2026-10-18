package br.com.projetotst_api.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "arquivo_certificado_treinamento")
public class ArquivoCertificadoTreinamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] conteudo;

    protected ArquivoCertificadoTreinamento() {
    }

    public ArquivoCertificadoTreinamento(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public byte[] getConteudo() {
        return conteudo;
    }
}
