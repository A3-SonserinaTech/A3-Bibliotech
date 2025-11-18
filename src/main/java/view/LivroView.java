package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import dao.LivroDAO;
import model.Livro;
import java.util.List;

public class LivroView extends JFrame {

    private JTable tabelaLivros;
    private DefaultTableModel tableModel;

    public LivroView() {
        super("Gerenciamento de Livros");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela, não o app todo
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        // Painel superior para botões de ação
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 1. CRIA os botões primeiro
        JButton btnAdicionar = new JButton("Adicionar Livro");
        JButton btnEditar = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");

        // 2. CONFIGURA as ações (Listeners)
        btnAdicionar.addActionListener(e -> adicionarLivro());
        btnExcluir.addActionListener(e -> excluirLivro());
        btnEditar.addActionListener(e -> editarLivro());

        // 3. ADICIONA ao painel
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.NORTH);

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
        // Limpa linhas antigas
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
        // 1. Criar os campos do formulário
        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        JTextField txtIsbn = new JTextField();
        JTextField txtAno = new JTextField();

        // 2. Montar o painel com os campos
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Título:"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Autor:"));
        panel.add(txtAutor);
        panel.add(new JLabel("ISBN:"));
        panel.add(txtIsbn);
        panel.add(new JLabel("Ano:"));
        panel.add(txtAno);

        // 3. Mostrar o diálogo e esperar o usuário clicar em OK
        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Novo Livro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // 4. Capturar dados
                String titulo = txtTitulo.getText();
                String autor = txtAutor.getText();
                String isbn = txtIsbn.getText();
                int ano = Integer.parseInt(txtAno.getText()); // Pode dar erro se não for número

                // 5. Salvar no banco
                Livro novoLivro = new Livro(titulo, autor, isbn, ano);
                LivroDAO dao = new LivroDAO();

                if (dao.adicionarLivro(novoLivro)) {
                    JOptionPane.showMessageDialog(this, "Livro salvo com sucesso!");
                    carregarTabela(); // Atualiza a tabela na hora
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar livro.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "O Ano deve ser um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void excluirLivro() {
        // 1. Verifica qual linha está selecionada
        int linhaSelecionada = tabelaLivros.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para excluir.");
            return;
        }

        // 2. Pega o ID da coluna 0 daquela linha
        int idLivro = (int) tabelaLivros.getValueAt(linhaSelecionada, 0);

        // 3. Pede confirmação
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o livro selecionado?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            LivroDAO dao = new LivroDAO();
            if (dao.removerLivro(idLivro)) {
                JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!");
                carregarTabela(); // Atualiza a lista visualmente
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir o livro.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void editarLivro() {
        int linhaSelecionada = tabelaLivros.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para editar.");
            return;
        }

        // 1. Recupera os dados da linha selecionada na tabela
        int id = (int) tabelaLivros.getValueAt(linhaSelecionada, 0);
        String tituloAtual = (String) tabelaLivros.getValueAt(linhaSelecionada, 1);
        String autorAtual = (String) tabelaLivros.getValueAt(linhaSelecionada, 2);
        String isbnAtual = (String) tabelaLivros.getValueAt(linhaSelecionada, 3);
        int anoAtual = (int) tabelaLivros.getValueAt(linhaSelecionada, 4);
        String statusTexto = (String) tabelaLivros.getValueAt(linhaSelecionada, 5);
        boolean disponivel = statusTexto.equals("Disponível");

        // 2. Cria os campos JÁ PREENCHIDOS com os dados atuais
        JTextField txtTitulo = new JTextField(tituloAtual);
        JTextField txtAutor = new JTextField(autorAtual);
        JTextField txtIsbn = new JTextField(isbnAtual);
        JTextField txtAno = new JTextField(String.valueOf(anoAtual));

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Título:"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Autor:"));
        panel.add(txtAutor);
        panel.add(new JLabel("ISBN:"));
        panel.add(txtIsbn);
        panel.add(new JLabel("Ano:"));
        panel.add(txtAno);

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                "Editar Livro", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // 3. Captura os novos dados
                String novoTitulo = txtTitulo.getText();
                String novoAutor = txtAutor.getText();
                String novoIsbn = txtIsbn.getText();
                int novoAno = Integer.parseInt(txtAno.getText());

                // 4. Cria objeto com o ID original (importante para o UPDATE funcionar)
                Livro livroEditado = new Livro(id, novoTitulo, novoAutor, novoIsbn, novoAno, disponivel);

                LivroDAO dao = new LivroDAO();
                if (dao.atualizarLivro(livroEditado)) {
                    JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ano inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
