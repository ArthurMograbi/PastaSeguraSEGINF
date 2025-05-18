package model;

public class Grupo {
    private Integer gid;
    private String nomeGrupo;

    // Construtor para criação de novos grupos (sem ID)
    public Grupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    // Construtor completo (para leitura do banco)
    public Grupo(Integer gid, String nomeGrupo) {
        this.gid = gid;
        this.nomeGrupo = nomeGrupo;
    }

    // Getters e Setters
    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "gid=" + gid +
                ", nomeGrupo='" + nomeGrupo + '\'' +
                '}';
    }
}