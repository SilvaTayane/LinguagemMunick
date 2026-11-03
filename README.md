# Munick — Analisador Léxico (Java / Maven)

Analisador léxico da linguagem **Munick**, executado em linha de comando.
Não possui dependências externas (apenas a biblioteca padrão do Java).

## Requisitos

- JDK 17 ou superior
- Maven 3.6 ou superior

## Compilar

```
mvn clean package
```

Gera `target/munick-lexer.jar`.

## Executar

Informe o nome do arquivo a ser analisado:

```
java -jar target/munick-lexer.jar exemplos/exemplo.mun
java -jar target/munick-lexer.jar exemplos/exemplo_erros.mun
```

Códigos de saída: `0` sem erros léxicos, `1` erro de uso/arquivo, `2` há erros léxicos.

## O que o analisador reconhece

| Categoria | Tokens |
|---|---|
| Palavras reservadas | `program const record module void ref int float bool char string if else while do for return read write filter average item true false` |
| Operadores aritméticos | `+ - * / div % ** ++ --` |
| Operadores lógicos | `and or not xor` |
| Operadores relacionais | `== != > < >= <=` |
| Atribuição | `=` |
| Delimitadores | `{ } ( ) [ ] ; , .` |
| Identificador | `[a-zA-Z_][a-zA-Z0-9_]*` |
| Literais | inteiro `[0-9]+`, real `[0-9]+.[0-9]+`, string `"..."`, char `'x'` |

Comentários de linha (`// ...`) e espaços em branco são descartados.
Escapes aceitos em string/char: `\n \t \r \0 \\ \" \'`.

## Erros léxicos detectados

Número mal formado (`9abc`), string não terminada, literal char vazio/não terminado/com mais de um
caractere, sequência de escape inválida, `!` isolado e caracteres inválidos. A análise **continua**
após cada erro, e todos são listados ao final com linha e coluna.

## Estrutura

```
munick-lexer/
├── pom.xml
├── README.md
├── exemplos/
│   ├── exemplo.mun            programa válido (usa todos os recursos da linguagem)
│   └── exemplo_erros.mun      programa com erros léxicos propositais
└── src/main/java/br/munick/lexer/
    ├── Main.java              linha de comando e impressão da tabela de tokens
    ├── Lexer.java             analisador léxico
    ├── Token.java             token (tipo, lexema, linha, coluna)
    ├── TokenType.java         tipos e categorias de token
    └── LexicalError.java      erro léxico (mensagem, linha, coluna)
```
