import database.DatabaseManager;

public class Main {

    public static void main(String[] args) {
        // Chama nosso método para criar o banco e as tabelas
        DatabaseManager.inicializarBanco();
    }
}