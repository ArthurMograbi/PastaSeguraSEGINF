package model;


public class Mensagem {
    private Integer mid;
    private Integer codigo;
    private String mensagem;

    // Construtor para criação (sem MID)
    public Mensagem(Integer codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    // Construtor completo (para leitura do banco)
    public Mensagem(Integer mid, Integer codigo, String mensagem) {
        this.mid = mid;
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "mid=" + mid +
                ", codigo=" + codigo +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}