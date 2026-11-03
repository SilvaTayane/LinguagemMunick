package br.munick.lexer;

/**
 * Todos os tipos de token da linguagem Munick.
 * Tokens com texto fixo (palavras reservadas, operadores e delimitadores)
 * possuem o campo {@code text}; as classes de token (id, números, literais)
 * não possuem texto fixo.
 */
public enum TokenType {

    // ---- Palavras reservadas ----
    PROGRAM("program", Category.PALAVRA_RESERVADA),
    CONST("const", Category.PALAVRA_RESERVADA),
    RECORD("record", Category.PALAVRA_RESERVADA),
    MODULE("module", Category.PALAVRA_RESERVADA),
    VOID("void", Category.PALAVRA_RESERVADA),
    REF("ref", Category.PALAVRA_RESERVADA),
    INT("int", Category.PALAVRA_RESERVADA),
    FLOAT("float", Category.PALAVRA_RESERVADA),
    BOOL("bool", Category.PALAVRA_RESERVADA),
    CHAR("char", Category.PALAVRA_RESERVADA),
    STRING("string", Category.PALAVRA_RESERVADA),
    IF("if", Category.PALAVRA_RESERVADA),
    ELSE("else", Category.PALAVRA_RESERVADA),
    WHILE("while", Category.PALAVRA_RESERVADA),
    DO("do", Category.PALAVRA_RESERVADA),
    FOR("for", Category.PALAVRA_RESERVADA),
    RETURN("return", Category.PALAVRA_RESERVADA),
    READ("read", Category.PALAVRA_RESERVADA),
    WRITE("write", Category.PALAVRA_RESERVADA),
    FILTER("filter", Category.PALAVRA_RESERVADA),
    AVERAGE("average", Category.PALAVRA_RESERVADA),
    ITEM("item", Category.PALAVRA_RESERVADA),
    TRUE("true", Category.PALAVRA_RESERVADA),
    FALSE("false", Category.PALAVRA_RESERVADA),

    // ---- Operadores lógicos (escritos como palavras) ----
    AND("and", Category.OPERADOR_LOGICO),
    OR("or", Category.OPERADOR_LOGICO),
    NOT("not", Category.OPERADOR_LOGICO),
    XOR("xor", Category.OPERADOR_LOGICO),

    // ---- Operadores aritméticos ----
    PLUS("+", Category.OPERADOR_ARITMETICO),
    MINUS("-", Category.OPERADOR_ARITMETICO),
    STAR("*", Category.OPERADOR_ARITMETICO),
    SLASH("/", Category.OPERADOR_ARITMETICO),
    DIV("div", Category.OPERADOR_ARITMETICO),
    PERCENT("%", Category.OPERADOR_ARITMETICO),
    POWER("**", Category.OPERADOR_ARITMETICO),
    INC("++", Category.OPERADOR_ARITMETICO),
    DEC("--", Category.OPERADOR_ARITMETICO),

    // ---- Operadores relacionais ----
    EQ("==", Category.OPERADOR_RELACIONAL),
    NEQ("!=", Category.OPERADOR_RELACIONAL),
    GT(">", Category.OPERADOR_RELACIONAL),
    LT("<", Category.OPERADOR_RELACIONAL),
    GE(">=", Category.OPERADOR_RELACIONAL),
    LE("<=", Category.OPERADOR_RELACIONAL),

    // ---- Atribuição ----
    ASSIGN("=", Category.ATRIBUICAO),

    // ---- Delimitadores e pontuação ----
    LBRACE("{", Category.DELIMITADOR),
    RBRACE("}", Category.DELIMITADOR),
    LPAREN("(", Category.DELIMITADOR),
    RPAREN(")", Category.DELIMITADOR),
    LBRACKET("[", Category.DELIMITADOR),
    RBRACKET("]", Category.DELIMITADOR),
    SEMICOLON(";", Category.DELIMITADOR),
    COMMA(",", Category.DELIMITADOR),
    DOT(".", Category.DELIMITADOR),

    // ---- Classes de tokens ----
    ID(null, Category.IDENTIFICADOR),
    NUM_INT(null, Category.LITERAL),
    NUM_FLOAT(null, Category.LITERAL),
    LIT_STRING(null, Category.LITERAL),
    LIT_CHAR(null, Category.LITERAL),

    // ---- Fim de arquivo ----
    EOF(null, Category.FIM_DE_ARQUIVO);

    /** Categorias usadas para agrupar os tokens na saída. */
    public enum Category {
        PALAVRA_RESERVADA,
        OPERADOR_ARITMETICO,
        OPERADOR_LOGICO,
        OPERADOR_RELACIONAL,
        ATRIBUICAO,
        DELIMITADOR,
        IDENTIFICADOR,
        LITERAL,
        FIM_DE_ARQUIVO
    }

    private final String text;
    private final Category category;

    TokenType(String text, Category category) {
        this.text = text;
        this.category = category;
    }

    /** Texto fixo do token, ou {@code null} se for uma classe de token. */
    public String text() {
        return text;
    }

    public Category category() {
        return category;
    }
}
