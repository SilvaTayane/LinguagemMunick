package br.munick.lexer;

/**
 * Erro léxico encontrado durante a análise.
 *
 * @param message descrição do erro
 * @param line    linha do erro (a partir de 1)
 * @param column  coluna do erro (a partir de 1)
 */
public record LexicalError(String message, int line, int column) {
}
