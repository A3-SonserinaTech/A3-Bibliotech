package model;

// Usamos java.time para datas modernas em Java
import java.time.LocalDate;

public class Emprestimo {

    private int id;
    private int idLivro;
    private int idUsuario;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucao; // Será nulo se ainda não foi devolvido

    // Construtor para criar um NOVO empréstimo
    public Emprestimo(int idLivro, int idUsuario) {
        this.idLivro = idLivro;
        this.idUsuario = idUsuario;
        this.dataEmprestimo = LocalDate.now(); // Pega a data de "hoje"
        this.dataDevolucao = null; // Começa como nulo
    }

    // Construtor para LER um empréstimo do banco
    public Emprestimo(int id, int idLivro, int idUsuario, String dataEmprestimoStr, String dataDevolucaoStr) {
        this.id = id;
        this.idLivro = idLivro;
        this.idUsuario = idUsuario;

        // Converte a String do banco (TEXT) para LocalDate
        this.dataEmprestimo = LocalDate.parse(dataEmprestimoStr);

        // Se a data de devolução não for nula no banco, converte
        if (dataDevolucaoStr != null) {
            this.dataDevolucao = LocalDate.parse(dataDevolucaoStr);
        } else {
            this.dataDevolucao = null;
        }
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLivro() {
        return idLivro;
    }

    public void setIdLivro(int idLivro) {
        this.idLivro = idLivro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    // Método 'is' para verificar se o empréstimo está ativo
    public boolean isAtivo() {
        return dataDevolucao == null;
    }

    // Método para facilitar a exibição
    @Override
    public String toString() {
        return "Emprestimo ID: " + id +
                ", Livro ID: " + idLivro +
                ", Usuário ID: " + idUsuario +
                ", Data: " + dataEmprestimo +
                (isAtivo() ? " (Ativo)" : " (Devolvido em " + dataDevolucao + ")");
    }
}