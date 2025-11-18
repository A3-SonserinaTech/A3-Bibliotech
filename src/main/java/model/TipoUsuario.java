package model;

/**
 * Define os tipos de permissão que um usuário pode ter no sistema.
 * BIBLIOTECARIO: Permissões administrativas (CRUD de livros, empréstimos, relatórios).
 * LEITOR: Permissões básicas (consulta de livros, solicitar empréstimo).
 */
public enum TipoUsuario {
    BIBLIOTECARIO,
    LEITOR
}