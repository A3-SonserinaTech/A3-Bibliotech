package dao;

import database.DatabaseManager;
import model.Usuario;
import model.TipoUsuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

class UsuarioDAOTest {

    private static UsuarioDAO usuarioDAO = new UsuarioDAO();

    @BeforeAll
    static void setUpGeral() {
        DatabaseManager.inicializarBanco();
    }

    @BeforeEach
    void setUpCadaTeste() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM usuarios");
        } catch (Exception e) {
            System.out.println("Erro ao limpar tabela: " + e.getMessage());
        }
    }

    @Test
    void testAdicionarEListarUsuario() {
        // Construtor com 5 argumentos: Nome, Email, Telefone, Senha, Tipo
        Usuario novoUsuario = new Usuario("Ana Silva", "ana.silva@email.com", "11987654321", "senha123", TipoUsuario.LEITOR);

        boolean sucessoAdd = usuarioDAO.adicionarUsuario(novoUsuario);
        List<Usuario> usuarios = usuarioDAO.listarTodosUsuarios();

        assertTrue(sucessoAdd);
        assertEquals(1, usuarios.size());
        assertEquals("Ana Silva", usuarios.get(0).getNome());
        assertEquals(TipoUsuario.LEITOR, usuarios.get(0).getTipoUsuario());
    }

    @Test
    void testAtualizarUsuario() {
        Usuario usuario = new Usuario("Carlos Lima", "carlos.lima@email.com", "21999998888", "admin456", TipoUsuario.BIBLIOTECARIO);
        usuarioDAO.adicionarUsuario(usuario);

        Usuario usuarioDoBanco = usuarioDAO.listarTodosUsuarios().get(0);
        usuarioDoBanco.setNome("Carlos Lima Santos");
        usuarioDoBanco.setTipoUsuario(TipoUsuario.LEITOR);

        boolean sucessoUpdate = usuarioDAO.atualizarUsuario(usuarioDoBanco);
        Usuario usuarioAtualizado = usuarioDAO.listarTodosUsuarios().get(0);

        assertTrue(sucessoUpdate);
        assertEquals("Carlos Lima Santos", usuarioAtualizado.getNome());
        assertEquals(TipoUsuario.LEITOR, usuarioAtualizado.getTipoUsuario());
    }

    @Test
    void testLoginSucessoEFalha() {
        Usuario admin = new Usuario("Admin", "admin@bib.com", "999999999", "admin123", TipoUsuario.BIBLIOTECARIO);
        usuarioDAO.adicionarUsuario(admin);

        Usuario usuarioLogado = usuarioDAO.buscarPorEmailESenha("admin@bib.com", "admin123");
        Usuario loginFalho = usuarioDAO.buscarPorEmailESenha("admin@bib.com", "senhaErrada");

        assertNotNull(usuarioLogado);
        assertEquals(TipoUsuario.BIBLIOTECARIO, usuarioLogado.getTipoUsuario());
        assertNull(loginFalho);
    }

    @Test
    void testRemoverUsuario() {
        Usuario usuario = new Usuario("Beatriz Costa", "bia.costa@email.com", "31888887777", "b4u", TipoUsuario.LEITOR);
        usuarioDAO.adicionarUsuario(usuario);

        int idParaRemover = usuarioDAO.listarTodosUsuarios().get(0).getId();

        boolean sucessoRemove = usuarioDAO.removerUsuario(idParaRemover);
        List<Usuario> usuarios = usuarioDAO.listarTodosUsuarios();

        assertTrue(sucessoRemove);
        assertEquals(0, usuarios.size());
    }
}