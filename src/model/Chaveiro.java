package model;

import java.time.LocalDateTime;

public class Chaveiro {
    private Integer kid;
    private Integer uid;
    private byte[] chavePrivada; // Chave privada criptografada (PKCS8)
    private String certificado;  // Certificado digital em formato PEM (BASE64)
    private LocalDateTime dataCriacao;

    // Construtor para leitura do banco (com todos os campos)
    public Chaveiro(Integer kid, Integer uid, byte[] chavePrivada, String certificado, LocalDateTime dataCriacao) {
        this.kid = kid;
        this.uid = uid;
        this.chavePrivada = chavePrivada;
        this.certificado = certificado;
        this.dataCriacao = dataCriacao;
    }

    // Construtor para inserção (sem KID e data de criação, gerados pelo banco)
    public Chaveiro(Integer uid, byte[] chavePrivada, String certificado) {
        this.uid = uid;
        this.chavePrivada = chavePrivada;
        this.certificado = certificado;
    }

    // Getters e Setters
    public Integer getKid() {
        return kid;
    }

    public void setKid(Integer kid) {
        this.kid = kid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public byte[] getChavePrivada() {
        return chavePrivada;
    }

    public void setChavePrivada(byte[] chavePrivada) {
        this.chavePrivada = chavePrivada;
    }

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    // Método toString para debug
    @Override
    public String toString() {
        return "Chaveiro{" +
                "kid=" + kid +
                ", uid=" + uid +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}