package view;

import dao.UsuarioDAO;
import model.TipoUsuario;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsuarioView extends JFrame {

    private JTable tabelaUsuarios;
    private DefaultTableModel tableModel;

    public UsuarioView() {
        super("Gerenciamento de Usuários");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarComponentes();
        carregarTabela(); // Carrega os dados ao abrir
    }

    private void inicializarComponentes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdicionar = new JButton("Adicionar Usuário");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        btnAdicionar.addActionListener(e -> adicionarUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnExcluir.addActionListener(e -> excluirUsuario());

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Email", "Telefone", "Tipo"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaUsuarios = new JTable(tableModel);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        UsuarioDAO dao = new UsuarioDAO();
        List<Usuario> lista = dao.listarTodosUsuarios();

        for (Usuario u : lista) {
            Object[] row = {
                    u.getId(),
                    u.getNome(),
                    u.getEmail(),
                    u.getTelefone(),
                    u.getTipoUsuario()
            };
            tableModel.addRow(row);
        }
    }

    private void adicionarUsuario() {
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTelefone = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        // Caixa de seleção para o Tipo
        JComboBox<TipoUsuario> cbTipo = new JComboBox<>(TipoUsuario.values());

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Nome:")); panel.add(txtNome);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Telefone:")); panel.add(txtTelefone);
        panel.add(new JLabel("Senha:")); panel.add(txtSenha);
        panel.add(new JLabel("Tipo:")); panel.add(cbTipo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Usuário", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Usuario novoUsuario = new Usuario(
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtTelefone.getText(),
                    new String(txtSenha.getPassword()),
                    (TipoUsuario) cbTipo.getSelectedItem()
            );

            UsuarioDAO dao = new UsuarioDAO();
            if (dao.adicionarUsuario(novoUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuário salvo!");
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return;
        }

        // Recupera dados da tabela (o ID é crucial)
        int id = (int) tabelaUsuarios.getValueAt(linha, 0);
        String nomeAtual = (String) tabelaUsuarios.getValueAt(linha, 1);
        String emailAtual = (String) tabelaUsuarios.getValueAt(linha, 2);
        String telAtual = (String) tabelaUsuarios.getValueAt(linha, 3);
        TipoUsuario tipoAtual = (TipoUsuario) tabelaUsuarios.getValueAt(linha, 4);

        // Preenche o formulário
        JTextField txtNome = new JTextField(nomeAtual);
        JTextField txtEmail = new JTextField(emailAtual);
        JTextField txtTelefone = new JTextField(telAtual);
        JPasswordField txtSenha = new JPasswordField(); // Senha vem vazia por segurança
        JComboBox<TipoUsuario> cbTipo = new JComboBox<>(TipoUsuario.values());
        cbTipo.setSelectedItem(tipoAtual);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Nome:")); panel.add(txtNome);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Telefone:")); panel.add(txtTelefone);
        panel.add(new JLabel("Nova Senha:")); panel.add(txtSenha);
        panel.add(new JLabel("Tipo:")); panel.add(cbTipo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Usuário", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Se a senha estiver vazia, manteríamos a antiga, mas para simplificar, exigimos nova senha
            String novaSenha = new String(txtSenha.getPassword());
            if (novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite a senha para confirmar a alteração.");
                return;
            }

            Usuario usuarioEditado = new Usuario(
                    id,
                    txtNome.getText(),
                    txtEmail.getText(),
                    txtTelefone.getText(),
                    novaSenha,
                    (TipoUsuario) cbTipo.getSelectedItem()
            );

            UsuarioDAO dao = new UsuarioDAO();
            if (dao.atualizarUsuario(usuarioEditado)) {
                JOptionPane.showMessageDialog(this, "Atualizado com sucesso!");
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário.");
            return;
        }

        int id = (int) tabelaUsuarios.getValueAt(linha, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza? Isso removerá o acesso deste usuário.", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UsuarioDAO dao = new UsuarioDAO();
            if (dao.removerUsuario(id)) {
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}