package database;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @Test
    void testCriacaoDoArquivoDeBanco() {
        // 1. Garante que o método de inicialização rode sem erros
        assertDoesNotThrow(() -> DatabaseManager.inicializarBanco());

        // 2. Verifica se o arquivo físico "biblioteca.db" foi criado na raiz
        File arquivoBanco = new File("biblioteca.db");
        assertTrue(arquivoBanco.exists(), "O arquivo biblioteca.db deveria existir na raiz do projeto.");
    }

    @Test
    void testConexaoDireta() {
        // Tenta abrir uma conexão manual para ver se o driver SQLite está funcionando
        String url = "jdbc:sqlite:biblioteca.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            assertNotNull(conn, "A conexão não deveria ser nula.");
            assertTrue(conn.isValid(5), "A conexão deveria estar válida/aberta.");
        } catch (SQLException e) {
            fail("Não deveria dar erro ao conectar no banco: " + e.getMessage());
        }
    }
}