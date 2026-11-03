package br.munick.lexer;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Programa principal: executa o analisador léxico sobre o arquivo informado
 * na linha de comando e imprime a tabela de tokens e os erros léxicos.
 *
 * Uso: java -jar munick-lexer.jar &lt;arquivo.mun&gt;
 *
 * Códigos de saída: 0 = sem erros léxicos; 1 = erro de uso ou de leitura do
 * arquivo; 2 = o arquivo possui erros léxicos.
 */
public final class Main {

    private static final String LINHA = "-".repeat(78);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java -jar munick-lexer.jar <arquivo.mun>");
            System.exit(1);
        }

        Path arquivo = Path.of(args[0]);
        if (!Files.isRegularFile(arquivo)) {
            System.err.println("Erro: arquivo não encontrado: " + args[0]);
            System.exit(1);
        }

        String fonte;
        try {
            fonte = Files.readString(arquivo, StandardCharsets.UTF_8);
        } catch (CharacterCodingException e) {
            System.err.println("Erro: o arquivo não está codificado em UTF-8: " + args[0]);
            System.exit(1);
            return;
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            System.exit(1);
            return;
        }

        Lexer lexer = new Lexer(fonte);
        List<Token> tokens = lexer.tokenize();
        List<LexicalError> erros = lexer.getErrors();

        System.out.println("ANÁLISE LÉXICA - arquivo: " + arquivo.getFileName());
        System.out.println(LINHA);
        System.out.printf("%-6s %-5s %-20s %-12s %s%n", "LINHA", "COL", "CATEGORIA", "TOKEN", "LEXEMA");
        System.out.println(LINHA);

        Map<TokenType.Category, Integer> contagem = new EnumMap<>(TokenType.Category.class);
        for (Token t : tokens) {
            if (t.type() == TokenType.EOF) {
                continue;
            }
            System.out.printf("%-6d %-5d %-20s %-12s %s%n",
                    t.line(), t.column(), t.type().category(), t.type(), t.lexeme());
            contagem.merge(t.type().category(), 1, Integer::sum);
        }

        System.out.println(LINHA);
        System.out.println("Total de tokens: " + (tokens.size() - 1));
        for (Map.Entry<TokenType.Category, Integer> e : contagem.entrySet()) {
            System.out.printf("  %-20s %d%n", e.getKey(), e.getValue());
        }

        System.out.println(LINHA);
        if (erros.isEmpty()) {
            System.out.println("Nenhum erro léxico encontrado.");
        } else {
            System.out.println("Erros léxicos: " + erros.size());
            for (LexicalError e : erros) {
                System.out.printf("  [ERRO] linha %d, coluna %d: %s%n", e.line(), e.column(), e.message());
            }
            System.exit(2);
        }
    }

    private Main() {
    }
}
