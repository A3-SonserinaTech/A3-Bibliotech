package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import dao.LivroDAO;
import model.Livro;
import model.TipoUsuario;
import model.Usuario;
import java.util.List;

public class LivroView extends JFrame {

    private JTable tabelaLivros;
    private DefaultTableModel tableModel;

    // Botões agora são atributos da classe para podermos esconder depois
    private JButton btnAdicionar;
    private JButton btnEditar;
    private JButton btnExcluir;

    // Construtor atualizado: Recebe o usuário logado
    public LivroView(Usuario usuarioLogado) {
        super("Gerenciamento de Livros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarComponentes(usuarioLogado); // Passa o usuário para a configuração
        carregarTabela();
    }

    private void inicializarComponentes(Usuario usuarioLogado) {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 1. CRIA os botões
        btnAdicionar = new JButton("Adicionar Livro");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");

        // 2. CONFIGURA as ações
        btnAdicionar.addActionListener(e -> adicionarLivro());
        btnExcluir.addActionListener(e -> excluirLivro());
        btnEditar.addActionListener(e -> editarLivro());

        // 3. ADICIONA ao painel
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.NORTH);

        // --- LÓGICA DE SEGURANÇA ---
        // Se o usuário for LEITOR, esconde os botões de modificação
        if (usuarioLogado.getTipoUsuario() == TipoUsuario.LEITOR) {
            btnAdicionar.setVisible(false);
            btnEditar.setVisible(false);
            btnExcluir.setVisible(false);
        }
        // ---------------------------

        // Configuração da Tabela
        String[] colunas = {"ID", "Título", "Autor", "ISBN", "Ano", "Status"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaLivros = new JTable(tableModel);
        tabelaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaLivros);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        LivroDAO dao = new LivroDAO();
        List<Livro> listaLivros = dao.listarTodosLivros();

        for (Livro livro : listaLivros) {
            Object[] row = {
                    livro.getId(),
                    livro.getTitulo(),
                    livro.getAutor(),
                    livro.getIsbn(),
                    livro.getAno(),
                    livro.isDisponivel() ? "Disponível" : "Emprestado"
            };
            tableModel.addRow(row);
        }
    }

    private void adicionarLivro() {
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtAno = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Título:")); panel.add(txtTitulo);
        panel.add(new JLabel("Autor:")); panel.add(txtAutor);
        panel.add(new JLabel("ISBN:")); panel.add(txtIsbn);
        panel.add(new JLabel("Ano:")); panel.add(txtAno);

        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Livro", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Livro novo = new Livro(txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), Integer.parseInt(txtAno.getText()));
                if (new LivroDAO().adicionarLivro(novo)) {
                    carregarTabela();
                    JOptionPane.showMessageDialog(this, "Livro salvo!");
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ano inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirLivro() {
        int linha = tabelaLivros.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro.");
            return;
        }
        int id = (int) tabelaLivros.getValueAt(linha, 0);
        if (JOptionPane.showConfirmDialog(this, "Tem certeza?", "Excluir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (new LivroDAO().removerLivro(id)) {
                carregarTabela();
                JOptionPane.showMessageDialog(this, "Excluído!");
            }
        }
    }

    private void editarLivro() {
        int linha = tabelaLivros.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro.");
            return;
        }

        // Recupera dados
        int id = (int) tabelaLivros.getValueAt(linha, 0);
        String titulo = (String) tabelaLivros.getValueAt(linha, 1);
        String autor = (String) tabelaLivros.getValueAt(linha, 2);
        String isbn = (String) tabelaLivros.getValueAt(linha, 3);
        int ano = (int) tabelaLivros.getValueAt(linha, 4);
        String status = (String) tabelaLivros.getValueAt(linha, 5);
        boolean disponivel = status.equals("Disponível");

        // Preenche campos
        JTextField txtTitulo = new JTextField(titulo);
        JTextField txtAutor = new JTextField(autor);
        JTextField txtIsbn = new JTextField(isbn);
        JTextField txtAno = new JTextField(String.valueOf(ano));

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Título:")); panel.add(txtTitulo);
        panel.add(new JLabel("Autor:")); panel.add(txtAutor);
        panel.add(new JLabel("ISBN:")); panel.add(txtIsbn);
        panel.add(new JLabel("Ano:")); panel.add(txtAno);

        if (JOptionPane.showConfirmDialog(this, panel, "Editar", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Livro editado = new Livro(id, txtTitulo.getText(), txtAutor.getText(), txtIsbn.getText(), Integer.parseInt(txtAno.getText()), disponivel);
                if (new LivroDAO().atualizarLivro(editado)) {
                    carregarTabela();
                    JOptionPane.showMessageDialog(this, "Atualizado!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro nos dados.");
            }
        }
    }
}