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
        setSize(400, 350); // Aumentei o tamanho para caber a logo e campos
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Mudei o layout principal para BorderLayout para colocar a logo em cima
        setLayout(new BorderLayout(10, 10)); // Espaçamento entre as áreas

        java.net.URL imgUrl = getClass().getResource("/img/logo.png");

        if (imgUrl != null) {
            // Se achou o arquivo, carrega a imagem
            ImageIcon logoIcon = new ImageIcon(imgUrl);

            Image imagem = logoIcon.getImage();
            Image imagemRedimensionada = imagem.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(imagemRedimensionada);

            JLabel lblLogo = new JLabel(logoIcon);
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            add(lblLogo, BorderLayout.NORTH);
        } else {
            // Se NÃO achou (para não travar o programa), mostra apenas um texto
            System.err.println("AVISO: Imagem não encontrada no caminho /img/logo.png");
            JLabel lblTitulo = new JLabel("BIBLIOTECH");
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
            lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
            // Adiciona margem para o texto não ficar colado no topo
            lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            add(lblTitulo, BorderLayout.NORTH);
        }
        // --- Fim da Logo ---


        // --- Painel para os campos de Login (Centro da Janela) ---
        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10)); // Grade 3x2 para email, senha e botão
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margem interna

        // Componentes de Login
        JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField();

        JLabel lblSenha = new JLabel("Senha:");
        txtSenha = new JPasswordField();

        btnLogin = new JButton("Entrar");

        // Adicionando componentes ao painel de campos
        panelCampos.add(lblEmail);
        panelCampos.add(txtEmail);
        panelCampos.add(lblSenha);
        panelCampos.add(txtSenha);
        panelCampos.add(new JLabel()); // Espaço vazio para alinhar o botão
        panelCampos.add(btnLogin);

        add(panelCampos, BorderLayout.CENTER); // Adiciona o painel de campos no centro da janela


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

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogado = dao.buscarPorEmailESenha(email, senha);

        if (usuarioLogado != null) {
            this.dispose();
            DashboardView dashboard = new DashboardView(usuarioLogado);
            dashboard.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Email ou senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}