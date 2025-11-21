package dao;

import model.Emprestimo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    private static final String DATABASE_URL = "jdbc:sqlite:biblioteca.db";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    /**
     * Realiza um novo empréstimo.
     * Isso é uma transação: insere no 'emprestimos' e atualiza 'livros'.
     */
    public boolean realizarEmprestimo(Emprestimo emprestimo) {
        String sqlEmprestimo = "INSERT INTO emprestimos(id_livro, id_usuario, data_emprestimo) VALUES(?, ?, ?)";
        String sqlUpdateLivro = "UPDATE livros SET disponivel = 0 WHERE id = ?"; // 0 = false
        Connection conn = null;

        try {
            conn = conectar();
            // Desliga o auto-commit para controlar a transação
            conn.setAutoCommit(false);

            // 1. Inserir o registro de empréstimo
            try (PreparedStatement pstmt = conn.prepareStatement(sqlEmprestimo)) {
                pstmt.setInt(1, emprestimo.getIdLivro());
                pstmt.setInt(2, emprestimo.getIdUsuario());
                pstmt.setString(3, emprestimo.getDataEmprestimo().toString()); // Salva como "AAAA-MM-DD"
                pstmt.executeUpdate();
            }

            // 2. Atualizar o status do livro para "indisponível"
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateLivro)) {
                pstmt.setInt(1, emprestimo.getIdLivro());
                pstmt.executeUpdate();
            }


            conn.commit(); // Confirma a transação
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao realizar empréstimo: " + e.getMessage());
            // Se algo deu errado, desfaz tudo
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz a transação
                } catch (SQLException ex) {
                    System.out.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            // Garante que o auto-commit seja religado
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Realiza a devolução de um livro.
     * Isso é uma transação: atualiza 'emprestimos' e atualiza 'livros'.
     */
    public boolean realizarDevolucao(int idEmprestimo, int idLivro) {
        String sqlEmprestimo = "UPDATE emprestimos SET data_devolucao = ? WHERE id = ?";
        String sqlUpdateLivro = "UPDATE livros SET disponivel = 1 WHERE id = ?"; // 1 = true
        Connection conn = null;

        try {
            conn = conectar();
            conn.setAutoCommit(false); // Inicia a transação

            // 1. Atualizar o empréstimo com a data de devolução
            try (PreparedStatement pstmt = conn.prepareStatement(sqlEmprestimo)) {
                pstmt.setString(1, LocalDate.now().toString()); // "Hoje"
                pstmt.setInt(2, idEmprestimo);
                pstmt.executeUpdate();
            }

            // 2. Atualizar o status do livro para "disponível"
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdateLivro)) {
                pstmt.setInt(1, idLivro);
                pstmt.executeUpdate();
            }

            conn.commit(); // Confirma
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao realizar devolução: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz
                } catch (SQLException ex) {
                    System.out.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Lista todos os empréstimos que ainda não foram devolvidos.
     */
    public List<Emprestimo> listarEmprestimosAtivos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        // Busca todos onde a data de devolução é NULA
        String sql = "SELECT * FROM emprestimos WHERE data_devolucao IS NULL";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Emprestimo emprestimo = new Emprestimo(
                        rs.getInt("id"),
                        rs.getInt("id_livro"),
                        rs.getInt("id_usuario"),
                        rs.getString("data_emprestimo"),
                        rs.getString("data_devolucao") // Será nulo
                );
                emprestimos.add(emprestimo);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar empréstimos ativos: " + e.getMessage());
        }
        return emprestimos;
    }
}