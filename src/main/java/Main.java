import dao.UsuarioDAO;
import database.DatabaseManager;
import model.TipoUsuario;
import model.Usuario;
import view.LoginView;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO SISTEMA BIBLIOTECH ===");

        // 1. Inicializa o banco
        DatabaseManager.inicializarBanco();

        UsuarioDAO dao = new UsuarioDAO();

        // 2. Tenta FORÇAR a criação do Admin (mesmo que dê erro de duplicidade)
        try {
            Usuario admin = new Usuario(
                    "Administrador",
                    "admin@email.com",
                    "999999999",
                    "admin123",
                    TipoUsuario.BIBLIOTECARIO
            );
            if (dao.adicionarUsuario(admin)) {
                System.out.println(">>> SUCESSO: Usuário Admin criado agora!");
            } else {
                System.out.println(">>> AVISO: Não foi possível criar Admin (provavelmente já existe email igual).");
            }
        } catch (Exception e) {
            System.out.println(">>> ERRO ao tentar criar admin: " + e.getMessage());
        }

        // 3. RELATÓRIO DE USUÁRIOS (O Espião)
        System.out.println("\n--- LISTA DE USUÁRIOS NO BANCO DE DADOS ---");
        List<Usuario> lista = dao.listarTodosUsuarios();
        if (lista.isEmpty()) {
            System.out.println("(O banco está VAZIO! Isso é estranho...)");
        } else {
            for (Usuario u : lista) {
                System.out.println("ID: " + u.getId());
                System.out.println("Nome: " + u.getNome());
                System.out.println("Email (LOGIN): " + u.getEmail());
                System.out.println("Senha: " + u.getSenha());
                System.out.println("Tipo: " + u.getTipoUsuario());
                System.out.println("----------------------------------");
            }
        }
        System.out.println("--- FIM DA LISTA ---\n");

        // 4. Abre a tela
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginView telaLogin = new LoginView();
            telaLogin.setVisible(true);
        });
    }
}