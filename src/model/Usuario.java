package model;


import java.time.LocalDateTime;


//Classe de modelagem para o usuário
public class Usuario {
    private Integer uid;
    private String email;
    private String nome;
    private Integer grupoGid;
    private String senhaPessoal;
    private byte[] chaveSecretaTOTP;
    private boolean bloqueado;
    private LocalDateTime dataBloqueio;
    private Integer kid;

    // Construtor padrão
    public Usuario() {}

    // Construtor para criação básica
    public Usuario(String email, String nome, Integer grupoGid, String senhaPessoal) {
        this.email = email;
        this.nome = nome;
        this.grupoGid = grupoGid;
        this.senhaPessoal = senhaPessoal;
    }

    // Getters e Setters
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getGrupoGid() {
        return grupoGid;
    }

    public void setGrupoGid(Integer grupoGid) {
        this.grupoGid = grupoGid;
    }

    public String getSenhaPessoal() {
        return senhaPessoal;   //Senha pessoal criptografada já
    }

    public void setSenhaPessoal(String senhaPessoal) {
        this.senhaPessoal = senhaPessoal;
    }

    public byte[] getChaveSecretaTOTP() {
        return chaveSecretaTOTP;
    }

    public void setChaveSecretaTOTP(byte[] chaveSecretaTOTP) {
        this.chaveSecretaTOTP = chaveSecretaTOTP;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public LocalDateTime getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(LocalDateTime dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public Integer getKid() {
        return kid;
    }

    public void setKid(Integer kid) {
        this.kid = kid;
    }

    // Método toString para debug (opcional)
    @Override
    public String toString() {
        return "Usuario{" +
                "uid=" + uid +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", grupoGid=" + grupoGid +
                ", bloqueado=" + bloqueado +
                '}';
    }
}