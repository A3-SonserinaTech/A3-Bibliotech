package model;

public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private int ano;
    private boolean disponivel;

    // Construtor vazio
    public Livro() {
    }

    // Construtor para inserir um NOVO livro (sem ID, pois o banco vai gerar)
    public Livro(String titulo, String autor, String isbn, int ano) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.ano = ano;
        this.disponivel = true; //Todo livro novo começa disponível
    }

    // Construtor para LER um livro do banco (com ID)
    public Livro(int id, String titulo, String autor, String isbn, int ano, boolean disponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.ano = ano;
        this.disponivel = disponivel;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    // Exibição em listas
    @Override
    public String toString() {
        return "ID: " + id + ", Título: " + titulo + ", Autor: " + autor + (disponivel ? " (Disponível)" : " (Emprestado)");
    }
}