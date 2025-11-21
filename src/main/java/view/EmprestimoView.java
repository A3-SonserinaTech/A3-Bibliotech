package view;

import dao.EmprestimoDAO;
import dao.LivroDAO;
import dao.UsuarioDAO;
import model.Emprestimo;
import model.Livro;
import model.TipoUsuario;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class EmprestimoView extends JFrame {

    private JTable tabelaEmprestimos;
    private DefaultTableModel tableModel;
    private Usuario usuarioLogado; // <-- NOVO: Guardamos quem está usando a tela

    // Construtor atualizado recebendo o usuarioLogado
    public EmprestimoView(Usuario usuarioLogado) {
        super("Gerenciamento de Empréstimos");
        this.usuarioLogado = usuarioLogado; // <-- NOVO: Armazena o usuário

        setSize(900, 600);
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

        btnNovo.addActionListener(e -> novoEmprestimo());
        btnDevolver.addActionListener(e -> devolverLivro());

        painelBotoes.add(btnNovo);
        // Apenas Bibliotecários podem registrar devolução (dar baixa no sistema)
        if (usuarioLogado.getTipoUsuario() == TipoUsuario.BIBLIOTECARIO) {
            painelBotoes.add(btnDevolver);
        }

        add(painelBotoes, BorderLayout.NORTH);

        String[] colunas = {"ID", "Livro", "Usuário", "Data Empréstimo", "Status"};

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

        // Pré-carrega listas para buscar nomes (traduzir ID para Nome)
        List<Livro> todosLivros = new LivroDAO().listarTodosLivros();
        List<Usuario> todosUsuarios = new UsuarioDAO().listarTodosUsuarios();

        for (Emprestimo emp : lista) {
            // --- 1. FILTRO DE SEGURANÇA ---
            // Se for Leitor e o empréstimo NÃO for dele, pula para o próximo (esconde)
            if (usuarioLogado.getTipoUsuario() == TipoUsuario.LEITOR && emp.getIdUsuario() != usuarioLogado.getId()) {
                continue;
            }

            // --- 2. TRADUÇÃO DE ID PARA NOME ---
            String tituloLivro = "ID " + emp.getIdLivro(); // Padrão caso não ache
            for (Livro l : todosLivros) {
                if (l.getId() == emp.getIdLivro()) {
                    tituloLivro = l.getTitulo();
                    break;
                }
            }

            String nomeUsuario = "ID " + emp.getIdUsuario(); // Padrão caso não ache
            for (Usuario u : todosUsuarios) {
                if (u.getId() == emp.getIdUsuario()) {
                    nomeUsuario = u.getNome();
                    break;
                }
            }

            Object[] row = {
                    emp.getId(),
                    tituloLivro, // Mostra o Título
                    nomeUsuario, // Mostra o Nome
                    emp.getDataEmprestimo(),
                    "Em Aberto"
            };
            tableModel.addRow(row);
        }
    }

    private void novoEmprestimo() {
        LivroDAO livroDAO = new LivroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Carrega livros disponíveis
        List<Livro> todosLivros = livroDAO.listarTodosLivros();
        Vector<Livro> livrosDisponiveis = new Vector<>();
        for (Livro l : todosLivros) {
            if (l.isDisponivel()) {
                livrosDisponiveis.add(l);
            }
        }

        if (livrosDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há livros disponíveis.");
            return;
        }

        // --- LÓGICA INTELIGENTE DE USUÁRIOS ---
        Vector<Usuario> listaUsuarios = new Vector<>();

        if (usuarioLogado.getTipoUsuario() == TipoUsuario.BIBLIOTECARIO) {
            // Se for Bibliotecário, pode escolher QUALQUER usuário do banco
            List<Usuario> todosUsuarios = usuarioDAO.listarTodosUsuarios();
            listaUsuarios.addAll(todosUsuarios);
        } else {
            // Se for Leitor, a lista só tem ELE MESMO
            listaUsuarios.add(usuarioLogado);
        }
        // --------------------------------------

        JComboBox<Livro> cbLivros = new JComboBox<>(livrosDisponiveis);
        JComboBox<Usuario> cbUsuarios = new JComboBox<>(listaUsuarios);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Selecione o Livro:"));
        panel.add(cbLivros);
        panel.add(new JLabel("Usuário:"));
        panel.add(cbUsuarios);

        int result = JOptionPane.showConfirmDialog(this, panel, "Novo Empréstimo", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Livro livroSelecionado = (Livro) cbLivros.getSelectedItem();
            Usuario usuarioSelecionado = (Usuario) cbUsuarios.getSelectedItem();

            if (livroSelecionado != null && usuarioSelecionado != null) {
                Emprestimo novo = new Emprestimo(livroSelecionado.getId(), usuarioSelecionado.getId());
                EmprestimoDAO dao = new EmprestimoDAO();

                if (dao.realizarEmprestimo(novo)) {
                    JOptionPane.showMessageDialog(this, "Sucesso!");
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao registrar.", "Erro", JOptionPane.ERROR_MESSAGE);
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

        // 1. Pega o ID do EMPRÉSTIMO (Coluna 0) - Este continua sendo número, então OK
        int idEmprestimo = (int) tabelaEmprestimos.getValueAt(linha, 0);

        // 2. Pega o NOME do Livro na tabela (Coluna 1) - Agora é String
        String nomeLivroNaTabela = (String) tabelaEmprestimos.getValueAt(linha, 1);

        // 3. Precisa descobrir qual é o ID desse livro
        int idLivro = -1;
        LivroDAO livroDAO = new LivroDAO();
        List<Livro> todosLivros = livroDAO.listarTodosLivros();

        for (Livro l : todosLivros) {
            if (l.getTitulo().equals(nomeLivroNaTabela)) {
                idLivro = l.getId();
                break;
            }
        }

        // Fallback: Se não achou pelo nome (ex: se o nome mudou), tenta pegar do objeto Emprestimo direto no banco
        if (idLivro == -1) {
            // Isso é raro, mas por segurança, vamos buscar o empréstimo original
            EmprestimoDAO empDao = new EmprestimoDAO();
            List<Emprestimo> lista = empDao.listarEmprestimosAtivos();
            for(Emprestimo e : lista) {
                if(e.getId() == idEmprestimo) {
                    idLivro = e.getIdLivro();
                    break;
                }
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar a devolução de '" + nomeLivroNaTabela + "'?", "Devolução", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            EmprestimoDAO dao = new EmprestimoDAO();

            if (dao.realizarDevolucao(idEmprestimo, idLivro)) {
                JOptionPane.showMessageDialog(this, "Livro devolvido com sucesso!");
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao registrar devolução.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}