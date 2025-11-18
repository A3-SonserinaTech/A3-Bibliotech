package view;

import model.TipoUsuario;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardView extends JFrame {

    // Recebemos o usuário logado para saber quem ele é
    public DashboardView(Usuario usuarioLogado) {

        setTitle("BIBLIOTECH - Menu Principal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. Cabeçalho (Topo) ---
        JPanel panelTopo = new JPanel();
        panelTopo.setBackground(new Color(70, 130, 180)); // Um azul bonito
        JLabel lblBemVindo = new JLabel("Bem-vindo, " + usuarioLogado.getNome() + " (" + usuarioLogado.getTipoUsuario() + ")");
        lblBemVindo.setForeground(Color.WHITE);
        lblBemVindo.setFont(new Font("Arial", Font.BOLD, 18));
        panelTopo.add(lblBemVindo);

        add(panelTopo, BorderLayout.NORTH);

        // --- 2. Área de Botões (Centro) ---
        JPanel panelBotoes = new JPanel();
        panelBotoes.setLayout(new GridLayout(2, 2, 20, 20)); // Grade 2x2 com espaços
        panelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margem

        JButton btnLivros = new JButton("Gerenciar Livros");
        JButton btnUsuarios = new JButton("Gerenciar Usuários");
        JButton btnEmprestimos = new JButton("Novo Empréstimo");
        JButton btnDevolucoes = new JButton("Registrar Devolução");

        // Só adiciona os botões ao painel
        panelBotoes.add(btnLivros);
        panelBotoes.add(btnUsuarios);
        panelBotoes.add(btnEmprestimos);
        panelBotoes.add(btnDevolucoes);

        add(panelBotoes, BorderLayout.CENTER);

        // --- 3. Ações dos Botões ---

        btnLivros.addActionListener(e -> {
            // CORREÇÃO: Passamos o 'usuarioLogado' para a tela de livros
            LivroView telaLivros = new LivroView(usuarioLogado);
            telaLivros.setVisible(true);
        });

        btnUsuarios.addActionListener(e -> {
            // Verifica se é Admin antes de abrir
            if (usuarioLogado.getTipoUsuario() == TipoUsuario.BIBLIOTECARIO) {
                UsuarioView telaUsuarios = new UsuarioView();
                telaUsuarios.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Acesso negado. Apenas Bibliotecários.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Abre a tela de empréstimos
        btnEmprestimos.addActionListener(e -> {
            EmprestimoView tela = new EmprestimoView();
            tela.setVisible(true);
        });

        // Abre a mesma tela de empréstimos (para devolução)
        btnDevolucoes.addActionListener(e -> {
            EmprestimoView tela = new EmprestimoView();
            tela.setVisible(true);
        });
    }
}