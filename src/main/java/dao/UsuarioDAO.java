package dao;

import model.Usuario;
import model.TipoUsuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String DATABASE_URL = "jdbc:sqlite:biblioteca.db";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public boolean adicionarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nome, email, telefone, senha, tipo_usuario) VALUES(?, ?, ?, ?, ?)"; // <-- SQL ATUALIZADO

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getTelefone());
            pstmt.setString(4, usuario.getSenha()); // <-- NOVO: Salva a senha
            pstmt.setString(5, usuario.getTipoUsuario().name()); // <-- NOVO: Salva o Enum como TEXT

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Erro ao adicionar usuário: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listarTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        // O bloco 'try-with-resources' agora fecha no local correto
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("senha"), // Lê a senha do DB
                        TipoUsuario.valueOf(rs.getString("tipo_usuario")) // Converte o texto do DB para o Enum TipoUsuario
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
        return usuarios;
    }
    public Usuario buscarPorEmailESenha(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, senha);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Retorna o objeto Usuario completo se encontrar
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("telefone"),
                            rs.getString("senha"),
                            TipoUsuario.valueOf(rs.getString("tipo_usuario"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuário para login: " + e.getMessage());
        }
        return null; // Retorna nulo se o login falhar
    }

        public boolean atualizarUsuario(Usuario usuario) {
            String sql = "UPDATE usuarios SET nome = ?, email = ?, telefone = ?, senha = ?, tipo_usuario = ? WHERE id = ?";

            try (Connection conn = conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, usuario.getNome());
                pstmt.setString(2, usuario.getEmail());
                pstmt.setString(3, usuario.getTelefone());
                pstmt.setString(4, usuario.getSenha()); // <-- NOVO: Salva a senha
                pstmt.setString(5, usuario.getTipoUsuario().name()); // <-- NOVO: Salva o TipoUsuario como TEXT
                pstmt.setInt(6, usuario.getId()); // O ID é usado no WHERE

                int linhasAfetadas = pstmt.executeUpdate();
                return linhasAfetadas > 0;

            } catch (SQLException e) {
                System.out.println("Erro ao atualizar usuário: " + e.getMessage());
                return false;
            }
        }


    public boolean removerUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao remover usuário: " + e.getMessage());
            return false;
        }
    }
}