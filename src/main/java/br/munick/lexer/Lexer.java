package br.munick.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analisador léxico da linguagem Munick.
 *
 * Reconhece palavras reservadas, identificadores, números inteiros e reais,
 * literais string e char, operadores e delimitadores. Espaços em branco e
 * comentários de linha ({@code // ...}) são descartados.
 *
 * Erros léxicos não interrompem a análise: são registrados e o analisador
 * continua a partir do ponto seguinte, de modo que todos os erros do arquivo
 * sejam informados de uma só vez.
 */
public final class Lexer {

    /** Sequências de escape aceitas em strings e chars (após a barra invertida). */
    private static final String ESCAPES_VALIDOS = "ntr0\\\"'";

    /** Palavras reservadas e operadores escritos como palavra (and, or, not, xor, div). */
    private static final Map<String, TokenType> PALAVRAS = new HashMap<>();

    static {
        for (TokenType t : TokenType.values()) {
            String texto = t.text();
            if (texto != null && ehLetra(texto.charAt(0))) {
                PALAVRAS.put(texto, t);
            }
        }
    }

    private final String src;
    private int pos;
    private int line;
    private int col;
    private final List<Token> tokens = new ArrayList<>();
    private final List<LexicalError> errors = new ArrayList<>();

    public Lexer(String source) {
        this.src = source;
    }

    /** Executa a análise e devolve a lista de tokens (terminada por EOF). */
    public List<Token> tokenize() {
        tokens.clear();
        errors.clear();
        pos = 0;
        line = 1;
        col = 1;

        while (!fim()) {
            lerToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, col));
        return List.copyOf(tokens);
    }

    /** Erros léxicos encontrados na última chamada de {@link #tokenize()}. */
    public List<LexicalError> getErrors() {
        return List.copyOf(errors);
    }

    // ------------------------------------------------------------------
    // Leitura de tokens
    // ------------------------------------------------------------------

    private void lerToken() {
        char c = olhar(0);

        // Espaços em branco
        if (c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f') {
            avancar();
            return;
        }

        // Comentário de linha
        if (c == '/' && olhar(1) == '/') {
            while (!fim() && olhar(0) != '\n') {
                avancar();
            }
            return;
        }

        if (ehLetra(c)) {
            lerIdentificador();
        } else if (ehDigito(c)) {
            lerNumero();
        } else if (c == '"') {
            lerString();
        } else if (c == '\'') {
            lerChar();
        } else {
            lerSimbolo(c);
        }
    }

    /** id : [a-zA-Z_][a-zA-Z0-9_]* (ou palavra reservada). */
    private void lerIdentificador() {
        int inicio = pos, l = line, c = col;
        while (ehLetra(olhar(0)) || ehDigito(olhar(0))) {
            avancar();
        }
        String lexema = src.substring(inicio, pos);
        TokenType tipo = PALAVRAS.getOrDefault(lexema, TokenType.ID);
        tokens.add(new Token(tipo, lexema, l, c));
    }

    /** num_int : [0-9]+   |   num_float : [0-9]+ '.' [0-9]+ */
    private void lerNumero() {
        int inicio = pos, l = line, c = col;
        while (ehDigito(olhar(0))) {
            avancar();
        }
        boolean real = false;
        if (olhar(0) == '.' && ehDigito(olhar(1))) {
            real = true;
            avancar();
            while (ehDigito(olhar(0))) {
                avancar();
            }
        }
        // Número seguido de letra ou '_' (ex.: 9abc, 1.5e3): número mal formado
        if (ehLetra(olhar(0))) {
            while (ehLetra(olhar(0)) || ehDigito(olhar(0))) {
                avancar();
            }
            erro("número mal formado '" + src.substring(inicio, pos) + "'", l, c);
            return;
        }
        tokens.add(new Token(real ? TokenType.NUM_FLOAT : TokenType.NUM_INT,
                src.substring(inicio, pos), l, c));
    }

    /** lit_string : '"' { caractere | escape } '"' (não pode atravessar a linha). */
    private void lerString() {
        int inicio = pos, l = line, c = col;
        avancar(); // aspas de abertura
        boolean fechou = false;
        boolean valido = true;

        while (!fim() && olhar(0) != '\n') {
            char ch = olhar(0);
            if (ch == '"') {
                avancar();
                fechou = true;
                break;
            }
            if (ch == '\\') {
                int el = line, ec = col;
                avancar();
                if (fim() || olhar(0) == '\n') {
                    break;
                }
                if (ESCAPES_VALIDOS.indexOf(olhar(0)) < 0) {
                    erro("sequência de escape inválida '\\" + olhar(0) + "'", el, ec);
                    valido = false;
                }
                avancar();
                continue;
            }
            avancar();
        }

        if (!fechou) {
            erro("string não terminada", l, c);
        } else if (valido) {
            tokens.add(new Token(TokenType.LIT_STRING, src.substring(inicio, pos), l, c));
        }
    }

    /** lit_char : "'" ( caractere | escape ) "'" */
    private void lerChar() {
        int inicio = pos, l = line, c = col;
        avancar(); // aspas simples de abertura

        if (olhar(0) == '\'') {
            avancar();
            erro("literal char vazio", l, c);
            return;
        }

        boolean fechou = false;
        boolean valido = true;
        int unidades = 0;

        while (!fim() && olhar(0) != '\n') {
            char ch = olhar(0);
            if (ch == '\'') {
                avancar();
                fechou = true;
                break;
            }
            if (ch == '\\') {
                int el = line, ec = col;
                avancar();
                if (fim() || olhar(0) == '\n') {
                    break;
                }
                if (ESCAPES_VALIDOS.indexOf(olhar(0)) < 0) {
                    erro("sequência de escape inválida '\\" + olhar(0) + "'", el, ec);
                    valido = false;
                }
                avancar();
                unidades++;
                continue;
            }
            avancar();
            unidades++;
        }

        if (!fechou) {
            erro("literal char não terminado", l, c);
        } else if (unidades != 1) {
            erro("literal char deve conter exatamente um caractere", l, c);
        } else if (valido) {
            tokens.add(new Token(TokenType.LIT_CHAR, src.substring(inicio, pos), l, c));
        }
    }

    /** Operadores e delimitadores (sempre pelo casamento mais longo). */
    private void lerSimbolo(char c) {
        int l = line, co = col;
        switch (c) {
            case '+' -> emitirDuplo('+', TokenType.INC, TokenType.PLUS, l, co);
            case '-' -> emitirDuplo('-', TokenType.DEC, TokenType.MINUS, l, co);
            case '*' -> emitirDuplo('*', TokenType.POWER, TokenType.STAR, l, co);
            case '/' -> simples(TokenType.SLASH, l, co);
            case '%' -> simples(TokenType.PERCENT, l, co);
            case '=' -> emitirDuplo('=', TokenType.EQ, TokenType.ASSIGN, l, co);
            case '>' -> emitirDuplo('=', TokenType.GE, TokenType.GT, l, co);
            case '<' -> emitirDuplo('=', TokenType.LE, TokenType.LT, l, co);
            case '!' -> {
                avancar();
                if (olhar(0) == '=') {
                    avancar();
                    tokens.add(new Token(TokenType.NEQ, "!=", l, co));
                } else {
                    erro("caractere '!' isolado (use '!=' ou 'not')", l, co);
                }
            }
            case '{' -> simples(TokenType.LBRACE, l, co);
            case '}' -> simples(TokenType.RBRACE, l, co);
            case '(' -> simples(TokenType.LPAREN, l, co);
            case ')' -> simples(TokenType.RPAREN, l, co);
            case '[' -> simples(TokenType.LBRACKET, l, co);
            case ']' -> simples(TokenType.RBRACKET, l, co);
            case ';' -> simples(TokenType.SEMICOLON, l, co);
            case ',' -> simples(TokenType.COMMA, l, co);
            case '.' -> simples(TokenType.DOT, l, co);
            default -> {
                avancar();
                erro("caractere inválido " + descrever(c), l, co);
            }
        }
    }

    /** Emite um token de um caractere. */
    private void simples(TokenType tipo, int l, int c) {
        avancar();
        tokens.add(new Token(tipo, tipo.text(), l, c));
    }

    /**
     * Emite o token duplo (ex.: "++") se o próximo caractere for {@code segundo};
     * caso contrário, emite o token simples (ex.: "+").
     */
    private void emitirDuplo(char segundo, TokenType duplo, TokenType simples, int l, int c) {
        avancar();
        if (olhar(0) == segundo) {
            avancar();
            tokens.add(new Token(duplo, duplo.text(), l, c));
        } else {
            tokens.add(new Token(simples, simples.text(), l, c));
        }
    }

    // ------------------------------------------------------------------
    // Utilitários
    // ------------------------------------------------------------------

    private boolean fim() {
        return pos >= src.length();
    }

    /** Olha o caractere à distância {@code n} sem consumir ('\0' se passar do fim). */
    private char olhar(int n) {
        int i = pos + n;
        return i < src.length() ? src.charAt(i) : '\0';
    }

    private char avancar() {
        char c = src.charAt(pos++);
        if (c == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        return c;
    }

    private void erro(String mensagem, int l, int c) {
        errors.add(new LexicalError(mensagem, l, c));
    }

    private static String descrever(char c) {
        if (Character.isISOControl(c)) {
            return String.format("(código U+%04X)", (int) c);
        }
        return "'" + c + "'";
    }

    private static boolean ehLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private static boolean ehDigito(char c) {
        return c >= '0' && c <= '9';
    }
}
