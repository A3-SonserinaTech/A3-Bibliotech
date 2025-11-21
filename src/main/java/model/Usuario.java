package model;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private String telefone;
    // NOVOS CAMPOS PARA LOGIN E PERMISSÃO
    private String senha;
    private TipoUsuario tipoUsuario;

    // Construtor vazio
    public Usuario() {
    }

    // Construtor para inserir (sem ID)
    public Usuario(String nome, String email, String telefone, String senha, TipoUsuario tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    // Construtor para ler (com ID)
    public Usuario(int id, String nome, String email, String telefone, String senha, TipoUsuario tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    // --- NOVOS Getters e Setters ---

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }


    @Override
    public String toString() {
        return "ID: " + id + ", Nome: " + nome + ", Tipo: " + tipoUsuario + ", Email: " + email;
    }
}