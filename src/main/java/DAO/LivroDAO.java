package dao;

import model.Livro;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    // URL de conexão com o banco de dados SQLite
    private static final String DATABASE_URL = "jdbc:sqlite:biblioteca.db";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }


     // Adiciona um novo livro ao banco de dados. O 'livro' recebido não tem ID, o banco vai gerar.

    public boolean adicionarLivro(Livro livro) {
        String sql = "INSERT INTO livros(titulo, autor, isbn, ano, disponivel) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getAutor());
            pstmt.setString(3, livro.getIsbn());
            pstmt.setInt(4, livro.getAno());
            pstmt.setBoolean(5, livro.isDisponivel());

            pstmt.executeUpdate();
            return true; // Sucesso

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar livro: " + e.getMessage());
            return false; // Falha
        }
    }


     //Retorna uma lista com todos os livros cadastrados.

    public List<Livro> listarTodosLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM livros";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Itera sobre o resultado (ResultSet)
            while (rs.next()) {
                Livro livro = new Livro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getInt("ano"),
                        rs.getBoolean("disponivel")
                );
                livros.add(livro);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros: " + e.getMessage());
        }
        return livros; // Retorna a lista (pode estar vazia)
    }


     //Atualiza os dados de um livro existente, baseado no ID.

    public boolean atualizarLivro(Livro livro) {
        String sql = "UPDATE livros SET titulo = ?, autor = ?, isbn = ?, ano = ?, disponivel = ? WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getAutor());
            pstmt.setString(3, livro.getIsbn());
            pstmt.setInt(4, livro.getAno());
            pstmt.setBoolean(5, livro.isDisponivel());
            pstmt.setInt(6, livro.getId()); // O ID é usado no WHERE

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se atualizou pelo menos 1 linha

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar livro: " + e.getMessage());
            return false;
        }
    }


     //Remove um livro do banco de dados, baseado no ID.
    public boolean removerLivro(int id) {
        String sql = "DELETE FROM livros WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se deletou pelo menos 1 linha

        } catch (SQLException e) {
            System.out.println("Erro ao remover livro: " + e.getMessage());
            return false;
        }
    }
}