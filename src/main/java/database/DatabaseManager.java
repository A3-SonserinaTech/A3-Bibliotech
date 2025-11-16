package database; // Esta linha informa que a classe está no pacote 'database'

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Esta é a definição da classe.
public class DatabaseManager {

    // Define o nome do arquivo do banco de dados (constante)
    private static final String DATABASE_URL = "jdbc:sqlite:biblioteca.db";

    public static void inicializarBanco() {

        // SQL para criar a tabela de Livros
        String sqlCreateTableLivros = "CREATE TABLE IF NOT EXISTS livros ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " titulo TEXT NOT NULL,"
                + " autor TEXT NOT NULL,"
                + " isbn TEXT UNIQUE,"
                + " ano INTEGER,"
                + " disponivel INTEGER DEFAULT 1" // 1 = true (disponível), 0 = false (emprestado)
                + ");";

        // criar a tabela de Usuários
        String sqlCreateTableUsuarios = "CREATE TABLE IF NOT EXISTS usuarios ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL,"
                + " email TEXT UNIQUE NOT NULL,"
                + " telefone TEXT"
                + ");";

        // criar a tabela de Empréstimos (relaciona Livros e Usuários)
        String sqlCreateTableEmprestimos = "CREATE TABLE IF NOT EXISTS emprestimos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_livro INTEGER NOT NULL,"
                + " id_usuario INTEGER NOT NULL,"
                + " data_emprestimo TEXT NOT NULL,"
                + " data_devolucao TEXT," // Fica nulo até o livro ser devolvido
                + " FOREIGN KEY (id_livro) REFERENCES livros(id),"
                + " FOREIGN KEY (id_usuario) REFERENCES usuarios(id)"
                + ");";

        // Este "try-with-resources" garante que a conexão (conn) e o statement (stmt)
        // sejam fechados automaticamente no final, mesmo se um erro ocorrer.
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {

            // Executa os comandos de criação de tabela
            stmt.execute(sqlCreateTableLivros);
            stmt.execute(sqlCreateTableUsuarios);
            stmt.execute(sqlCreateTableEmprestimos);

            System.out.println("Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {

            System.out.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}