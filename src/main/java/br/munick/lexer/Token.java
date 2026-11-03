package br.munick.lexer;

/**
 * Token reconhecido pelo analisador léxico.
 *
 * @param type   tipo do token
 * @param lexeme texto exato encontrado no código-fonte
 * @param line   linha onde o token começa (a partir de 1)
 * @param column coluna onde o token começa (a partir de 1)
 */
public record Token(TokenType type, String lexeme, int line, int column) {
}
