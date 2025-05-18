package model;


import java.time.LocalDateTime;


public class Registro {
    private Integer rid;
    private Integer uid;
    private Integer mid;
    private LocalDateTime dataRegistro;

    // Construtor para leitura do banco (com todos os campos)
    public Registro(Integer rid, Integer uid,Integer mid,LocalDateTime dataRegistro) {
        this.rid = rid;
        this.uid = uid;
        this.mid = mid;
        this.dataRegistro = dataRegistro;
    }

    // Construtor para inserção (sem UID e data de criação, gerados pelo banco)
    public Registro(Integer uid,Integer mid) {
        this.uid = uid;
        this.mid = mid;
    }

    // Getters e Setters
    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    // Método toString para debug
    @Override
    public String toString() {
        return "Registro{" +
                "kid=" + rid +
                ", uid=" + uid +
                ", mid=" + mid +
                ", dataRegistro=" + dataRegistro +
                '}';
    }
}