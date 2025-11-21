package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class ModelTest {

    @Test
    void testLivro() {
        // Testa o construtor e getters
        Livro livro = new Livro("Dom Casmurro", "Machado", "123", 1899);

        assertEquals("Dom Casmurro", livro.getTitulo());
        assertEquals("Machado", livro.getAutor());
        assertTrue(livro.isDisponivel());

        // Testa os setters
        livro.setTitulo("Memórias Póstumas");
        livro.setDisponivel(false);

        assertEquals("Memórias Póstumas", livro.getTitulo());
        assertFalse(livro.isDisponivel());

        // Testa o toString (para garantir que não quebra)
        assertNotNull(livro.toString());
    }

    @Test
    void testUsuario() {
        // Testa construtor completo
        Usuario user = new Usuario("João", "joao@email.com", "999", "senha123", TipoUsuario.LEITOR);

        assertEquals("João", user.getNome());
        assertEquals(TipoUsuario.LEITOR, user.getTipoUsuario());

        // Testa setters
        user.setNome("João Silva");
        user.setTipoUsuario(TipoUsuario.BIBLIOTECARIO);

        assertEquals("João Silva", user.getNome());
        assertEquals(TipoUsuario.BIBLIOTECARIO, user.getTipoUsuario());

        assertNotNull(user.toString());
    }

    @Test
    void testEmprestimo() {
        // Testa construtor
        Emprestimo emp = new Emprestimo(1, 2); // idLivro 1, idUsuario 2

        assertEquals(1, emp.getIdLivro());
        assertEquals(2, emp.getIdUsuario());
        assertEquals(LocalDate.now(), emp.getDataEmprestimo()); // Deve ser hoje
        assertTrue(emp.isAtivo()); // Deve estar ativo

        // Testa devolução
        emp.setDataDevolucao(LocalDate.now());
        assertFalse(emp.isAtivo()); // Não deve mais estar ativo

        assertNotNull(emp.toString());
    }
}