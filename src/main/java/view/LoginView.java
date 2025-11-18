package view;

import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnLogin;

    public LoginView() {
        // Configurações da Janela Principal
        setTitle("BIBLIOTECH - Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fecha o app ao clicar no X
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new GridLayout(4, 2, 10, 10)); // Layout em grade (linhas, colunas)

        // Criando os componentes
        JLabel lblEmail = new JLabel("  Email:");
        txtEmail = new JTextField();

        JLabel lblSenha = new JLabel("  Senha:");
        txtSenha = new JPasswordField();

        btnLogin = new JButton("Entrar");

        // Adicionando componentes na tela
        add(lblEmail);
        add(txtEmail);
        add(lblSenha);
        add(txtSenha);
        add(new JLabel()); // Espaço vazio para alinhar
        add(btnLogin);

        // Ação do Botão "Entrar"
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fazerLogin();
            }
        });
    }

    private void fazerLogin() {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        // Chama nosso DAO para verificar no banco de dados
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogado = dao.buscarPorEmailESenha(email, senha);

        if (usuarioLogado != null) {
            // 1. Fecha a janela de login
            this.dispose();

            // 2. Abre o Dashboard passando o usuário que logou
            DashboardView dashboard = new DashboardView(usuarioLogado);
            dashboard.setVisible(true);

        } else {

            JOptionPane.showMessageDialog(this, "Email ou senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}