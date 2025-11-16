package dao;

import database.DatabaseManager;
import model.Emprestimo;
import model.Livro;
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
        // Garante que o banco e as tabelas existem
        DatabaseManager.inicializarBanco();
    }

    @BeforeEach
    void setUpCadaTeste() {
        // Limpa TODAS as tabelas para um teste limpo
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM emprestimos");
            stmt.execute("DELETE FROM livros");
            stmt.execute("DELETE FROM usuarios");

        } catch (SQLException e) {
            System.out.println("Erro ao limpar tabelas para teste: " + e.getMessage());
        }
    }

    @Test
    void testRealizarEListarEmprestimo() {
        // 1. Preparar (Setup)
        // Precisamos de um livro e um usuário no banco primeiro
        Livro livro = new Livro("O Hobbit", "J.R.R. Tolkien", "12345", 1937);
        Usuario usuario = new Usuario("Marcelo", "marcelo@email.com", "912345678");
        livroDAO.adicionarLivro(livro);
        usuarioDAO.adicionarUsuario(usuario);

        // Pega os IDs gerados pelo banco
        int idLivro = livroDAO.listarTodosLivros().get(0).getId();
        int idUsuario = usuarioDAO.listarTodosUsuarios().get(0).getId();

        // 2. Agir (Act)
        Emprestimo novoEmprestimo = new Emprestimo(idLivro, idUsuario);
        boolean sucessoEmprestimo = emprestimoDAO.realizarEmprestimo(novoEmprestimo);

        // 3. Verificar (Assert)
        assertTrue(sucessoEmprestimo); // Verifica se o empréstimo retornou true
        assertEquals(1, emprestimoDAO.listarEmprestimosAtivos().size()); // Verifica se há 1 empréstimo ativo

        // Teste CRUCIAL da transação: Verifica se o livro ficou indisponível
        Livro livroEmprestado = livroDAO.listarTodosLivros().get(0);
        assertFalse(livroEmprestado.isDisponivel(), "O livro deveria estar marcado como indisponível.");
    }

    @Test
    void testRealizarDevolucao() {
        // 1. Preparar (Setup: realizar um empréstimo primeiro)
        Livro livro = new Livro("O Hobbit", "J.R.R. Tolkien", "12345", 1937);
        Usuario usuario = new Usuario("Marcelo", "marcelo@email.com", "912345678");
        livroDAO.adicionarLivro(livro);
        usuarioDAO.adicionarUsuario(usuario);

        int idLivro = livroDAO.listarTodosLivros().get(0).getId();
        int idUsuario = usuarioDAO.listarTodosUsuarios().get(0).getId();

        Emprestimo novoEmprestimo = new Emprestimo(idLivro, idUsuario);
        emprestimoDAO.realizarEmprestimo(novoEmprestimo);

        // Pega o ID do empréstimo que acabamos de criar
        int idEmprestimo = emprestimoDAO.listarEmprestimosAtivos().get(0).getId();

        // 2. Agir (Act)
        boolean sucessoDevolucao = emprestimoDAO.realizarDevolucao(idEmprestimo, idLivro);

        // 3. Verificar (Assert)
        assertTrue(sucessoDevolucao); // Verifica se a devolução retornou true
        assertEquals(0, emprestimoDAO.listarEmprestimosAtivos().size(), "Não deveria haver empréstimos ativos.");

        // Teste CRUCIAL da transação: Verifica se o livro ficou disponível
        Livro livroDevolvido = livroDAO.listarTodosLivros().get(0);
        assertTrue(livroDevolvido.isDisponivel(), "O livro deveria estar marcado como disponível.");
    }
}