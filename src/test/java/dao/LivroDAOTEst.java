package dao;

import database.DatabaseManager;
import model.Livro;
import org.junit.jupiter.api.BeforeAll; // Importante: org.junit.jupiter
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*; // Importa os 'asserts'

class LivroDAOTest {

    // Cria uma instância do DAO que será usada por todos os testes
    private static LivroDAO livroDAO = new LivroDAO();

    // Este método especial roda UMA VEZ antes de todos os testes da classe
    @BeforeAll
    static void setUp() {
        System.out.println("Configurando o banco de dados para testes...");
        // 1. Garante que as tabelas existam
        DatabaseManager.inicializarBanco();

        // 2. Limpa a tabela de livros para garantir que o teste comece "do zero"
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:biblioteca.db");
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM livros"); // Apaga todos os livros
            System.out.println("Tabela 'livros' limpa para o teste.");

        } catch (Exception e) {
            System.out.println("Erro ao limpar tabela de livros: " + e.getMessage());
        }
    }

    @Test // Marca este método como um teste que o JUnit deve rodar
    void testAdicionarEListarLivro() {
        System.out.println("Executando testAdicionarEListarLivro...");

        // 1. ARRANGE (Preparar)
        // Cria um objeto Livro para ser inserido
        Livro novoLivro = new Livro("O Senhor dos Anéis", "J.R.R. Tolkien", "978-0618640157", 1954);

        // 2. ACT (Agir)
        // Tenta adicionar o livro ao banco
        boolean sucessoAoAdicionar = livroDAO.adicionarLivro(novoLivro);

        // Tenta listar todos os livros do banco
        List<Livro> livros = livroDAO.listarTodosLivros();

        // 3. ASSERT (Verificar)
        // O teste passa se todas essas condições forem verdadeiras:

        // Verifica se o método adicionarLivro() retornou 'true'
        assertTrue(sucessoAoAdicionar, "O método adicionarLivro() deve retornar true.");

        // Verifica se a lista de livros não é nula
        assertNotNull(livros, "A lista de livros não pode ser nula.");

        // Verifica se a lista tem exatamente 1 livro
        assertEquals(1, livros.size(), "A lista deve conter exatamente 1 livro após a adição.");

        // Pega o livro que veio do banco e verifica seus dados
        Livro livroDoBanco = livros.get(0);
        assertEquals("O Senhor dos Anéis", livroDoBanco.getTitulo(), "O título do livro no banco não bate.");
        assertEquals("J.R.R. Tolkien", livroDoBanco.getAutor(), "O autor do livro no banco não bate.");
    }
}