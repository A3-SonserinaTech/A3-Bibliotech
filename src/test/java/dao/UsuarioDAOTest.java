package dao;

import database.DatabaseManager;
import model.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach; // Novo import
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

class UsuarioDAOTest {

    private static UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Roda uma vez antes de TODOS os testes
    @BeforeAll
    static void setUpGeral() {
        // Garante que o banco e as tabelas existem
        DatabaseManager.inicializarBanco();
    }

    // Roda ANTES DE CADA teste (@Test)
    @BeforeEach
    void setUpCadaTeste() {
        // Limpa a tabela de usuários para que um teste não interfira no outro
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM usuarios");

        } catch (Exception e) {
            System.out.println("Erro ao limpar tabela de usuários: " + e.getMessage());
        }
    }

    @Test
    void testAdicionarEListarUsuario() {
        // 1. Preparar
        Usuario novoUsuario = new Usuario("Ana Silva", "ana.silva@email.com", "11987654321");

        // 2. Agir
        boolean sucessoAdd = usuarioDAO.adicionarUsuario(novoUsuario);
        List<Usuario> usuarios = usuarioDAO.listarTodosUsuarios();

        // 3. Verificar
        assertTrue(sucessoAdd); // Verifica se a adição retornou true
        assertEquals(1, usuarios.size()); // Verifica se a lista tem 1 usuário
        assertEquals("Ana Silva", usuarios.get(0).getNome()); // Verifica o nome
    }

    @Test
    void testAtualizarUsuario() {
        // 1. Preparar (Adiciona um usuário primeiro)
        Usuario usuario = new Usuario("Carlos Lima", "carlos.lima@email.com", "21999998888");
        usuarioDAO.adicionarUsuario(usuario);

        // Pega o usuário do banco (para ter o ID)
        Usuario usuarioDoBanco = usuarioDAO.listarTodosUsuarios().get(0);

        // 2. Agir (Muda o nome e atualiza)
        usuarioDoBanco.setNome("Carlos Lima Santos");
        boolean sucessoUpdate = usuarioDAO.atualizarUsuario(usuarioDoBanco);

        // Pega o usuário atualizado
        Usuario usuarioAtualizado = usuarioDAO.listarTodosUsuarios().get(0);

        // 3. Verificar
        assertTrue(sucessoUpdate); // Verifica se o update retornou true
        assertEquals(1, usuarioDAO.listarTodosUsuarios().size()); // Garante que ainda só temos 1 usuário
        assertEquals("Carlos Lima Santos", usuarioAtualizado.getNome()); // Verifica se o nome mudou
    }

    @Test
    void testRemoverUsuario() {
        // 1. Preparar (Adiciona um usuário)
        Usuario usuario = new Usuario("Beatriz Costa", "bia.costa@email.com", "31888887777");
        usuarioDAO.adicionarUsuario(usuario);

        // Pega o ID do usuário adicionado
        int idParaRemover = usuarioDAO.listarTodosUsuarios().get(0).getId();

        // 2. Agir
        boolean sucessoRemove = usuarioDAO.removerUsuario(idParaRemover);
        List<Usuario> usuarios = usuarioDAO.listarTodosUsuarios();

        // 3. Verificar
        assertTrue(sucessoRemove); // Verifica se a remoção retornou true
        assertEquals(0, usuarios.size()); // Verifica se a lista está vazia
    }
}