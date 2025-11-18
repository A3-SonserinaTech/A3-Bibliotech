package dao;

import database.DatabaseManager;
import model.Emprestimo;
import model.Livro;
import model.TipoUsuario; // Importação nova
import model.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class EmprestimoDAOTest {

    private static EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
    private static LivroDAO livroDAO = new LivroDAO();
    private static UsuarioDAO usuarioDAO = new UsuarioDAO();

    @BeforeAll
    static void setUpGeral() {
        DatabaseManager.inicializarBanco();
    }

    @BeforeEach
    void setUpCadaTeste() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM emprestimos");
            stmt.execute("DELETE FROM livros");
            stmt.execute("DELETE FROM usuarios");
        } catch (SQLException e) {
            System.out.println("Erro ao limpar tabelas: " + e.getMessage());
        }
    }

    @Test
    void testRealizarEListarEmprestimo() {
        // 1. Preparar
        Livro livro = new Livro("O Hobbit", "J.R.R. Tolkien", "12345", 1937);
        // ATENÇÃO: Aqui passamos a senha "123456" e o tipo LEITOR
        Usuario usuario = new Usuario("Marcelo", "marcelo@email.com", "912345678", "123456", TipoUsuario.LEITOR);

        livroDAO.adicionarLivro(livro);
        usuarioDAO.adicionarUsuario(usuario);

        int idLivro = livroDAO.listarTodosLivros().get(0).getId();
        int idUsuario = usuarioDAO.listarTodosUsuarios().get(0).getId();

        // 2. Agir
        Emprestimo novoEmprestimo = new Emprestimo(idLivro, idUsuario);
        boolean sucessoEmprestimo = emprestimoDAO.realizarEmprestimo(novoEmprestimo);

        // 3. Verificar
        assertTrue(sucessoEmprestimo);
        assertEquals(1, emprestimoDAO.listarEmprestimosAtivos().size());

        Livro livroEmprestado = livroDAO.listarTodosLivros().get(0);
        assertFalse(livroEmprestado.isDisponivel());
    }

    @Test
    void testRealizarDevolucao() {
        // 1. Preparar
        Livro livro = new Livro("O Hobbit", "J.R.R. Tolkien", "12345", 1937);
        // ATENÇÃO: Atualizado aqui também
        Usuario usuario = new Usuario("Marcelo", "marcelo@email.com", "912345678", "123456", TipoUsuario.LEITOR);

        livroDAO.adicionarLivro(livro);
        usuarioDAO.adicionarUsuario(usuario);

        int idLivro = livroDAO.listarTodosLivros().get(0).getId();
        int idUsuario = usuarioDAO.listarTodosUsuarios().get(0).getId();

        Emprestimo novoEmprestimo = new Emprestimo(idLivro, idUsuario);
        emprestimoDAO.realizarEmprestimo(novoEmprestimo);

        int idEmprestimo = emprestimoDAO.listarEmprestimosAtivos().get(0).getId();

        // 2. Agir
        boolean sucessoDevolucao = emprestimoDAO.realizarDevolucao(idEmprestimo, idLivro);

        // 3. Verificar
        assertTrue(sucessoDevolucao);
        assertEquals(0, emprestimoDAO.listarEmprestimosAtivos().size());

        Livro livroDevolvido = livroDAO.listarTodosLivros().get(0);
        assertTrue(livroDevolvido.isDisponivel());
    }
}