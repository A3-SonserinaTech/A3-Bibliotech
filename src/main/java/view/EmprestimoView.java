package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import dao.EmprestimoDAO;
import model.Emprestimo;
import java.util.List;
import dao.LivroDAO;
import dao.UsuarioDAO;
import model.Livro;
import model.Usuario;
import java.util.Vector; // Usado para o JComboBox

public class EmprestimoView extends JFrame {

    private JTable tabelaEmprestimos;
    private DefaultTableModel tableModel;

    public EmprestimoView() {
        super("Gerenciamento de Empréstimos");
        setSize(900, 600); // Um pouco mais larga para caber as datas
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarComponentes();
        carregarTabela();
    }

    private void inicializarComponentes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnNovo = new JButton("Novo Empréstimo");
        JButton btnDevolver = new JButton("Registrar Devolução");

        // Ações (Listeners)
        btnNovo.addActionListener(e -> novoEmprestimo());
        btnDevolver.addActionListener(e -> devolverLivro());

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnDevolver);

        add(painelBotoes, BorderLayout.NORTH);

        // Colunas: ID, Livro, Usuário, Data Empréstimo, Data Devolução
        String[] colunas = {"ID", "ID Livro", "ID Usuário", "Data Empréstimo", "Status"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaEmprestimos = new JTable(tableModel);
        tabelaEmprestimos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelaEmprestimos);
        add(scrollPane, BorderLayout.CENTER);
    }
    private void carregarTabela() {
        tableModel.setRowCount(0);
        EmprestimoDAO dao = new EmprestimoDAO();
        List<Emprestimo> lista = dao.listarEmprestimosAtivos();

        for (Emprestimo emp : lista) {
            Object[] row = {
                    emp.getId(),
                    emp.getIdLivro(),
                    emp.getIdUsuario(),
                    emp.getDataEmprestimo(),
                    "Em Aberto"
            };
            tableModel.addRow(row);
        }
    }
    private void novoEmprestimo() {
        // 1. Buscas dados para preencher as caixas de seleção
        LivroDAO livroDAO = new LivroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        List<Livro> todosLivros = livroDAO.listarTodosLivros();
        List<Usuario> todosUsuarios = usuarioDAO.listarTodosUsuarios();

        // Vetores para o JComboBox
        Vector<Livro> livrosDisponiveis = new Vector<>();
        for (Livro l : todosLivros) {
            if (l.isDisponivel()) {
                livrosDisponiveis.add(l);
            }
        }

        Vector<Usuario> listaUsuarios = new Vector<>(todosUsuarios);

        if (livrosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há livros disponíveis para empréstimo.");
            return;
        }

        // 2. Cria os componentes visuais
        JComboBox<Livro> cbLivros = new JComboBox<>(livrosDisponiveis);
        JComboBox<Usuario> cbUsuarios = new JComboBox<>(listaUsuarios);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Selecione o Livro:"));
        panel.add(cbLivros);
        panel.add(new JLabel("Selecione o Usuário:"));
        panel.add(cbUsuarios);

        // 3. Mostra o diálogo
        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Empréstimo", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Livro livroSelecionado = (Livro) cbLivros.getSelectedItem();
            Usuario usuarioSelecionado = (Usuario) cbUsuarios.getSelectedItem();

            if (livroSelecionado != null && usuarioSelecionado != null) {
                Emprestimo novo = new Emprestimo(livroSelecionado.getId(), usuarioSelecionado.getId());
                EmprestimoDAO emprestimoDAO = new EmprestimoDAO();

                if (emprestimoDAO.realizarEmprestimo(novo)) {
                    JOptionPane.showMessageDialog(this, "Empréstimo registrado com sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao registrar empréstimo.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    private void devolverLivro() {
        int linha = tabelaEmprestimos.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo para devolver.");
            return;
        }

        // Recupera os IDs da linha selecionada
        int idEmprestimo = (int) tabelaEmprestimos.getValueAt(linha, 0);
        int idLivro = (int) tabelaEmprestimos.getValueAt(linha, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar a devolução deste livro?", "Devolução", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            EmprestimoDAO dao = new EmprestimoDAO();

            // Chama o método que atualiza a data de devolução e libera o livro
            if (dao.realizarDevolucao(idEmprestimo, idLivro)) {
                JOptionPane.showMessageDialog(this, "Livro devolvido com sucesso!");
                carregarTabela(); // Atualiza a lista (o empréstimo deve sumir pois não está mais "Em Aberto")
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao registrar devolução.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}