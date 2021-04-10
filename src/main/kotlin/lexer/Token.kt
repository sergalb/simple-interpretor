package lexer

enum class Type {
    COLON,
    COMMA,
    NUMBER,
    MOD,
    MUL,
    EQUAL,
    GREATER,
    LBRACE,
    LPAREN,
    LBRACKET,
    RBRACE,
    RBRACKET,
    RPAREN,
    MINUS,
    DIV,
    LESS,
    IDENTIFIER,
    QUESTION,
    PLUS,
    EOL,
    EOF
}

data class Token(val type: Type, val line: Int, var value: Any? = null, val children: MutableList<Token> = ArrayList()) {
    constructor(other: Token) : this(other.type, other.line, other.value, other.children)
}