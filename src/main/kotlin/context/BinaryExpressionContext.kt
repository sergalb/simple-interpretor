package context

enum class Operator {
    PLUS {
        override fun toString(): String = "+"
    },
    MINUS {
        override fun toString(): String = "-"
    },
    MUL {
        override fun toString(): String = "*"
    },
    DIV {
        override fun toString(): String = "/"
    },
    MOD {
        override fun toString(): String = "%"
    },
    LESS {
        override fun toString(): String = "<"
    },
    GREATER {
        override fun toString(): String = ">"
    },
    EQUAL {
        override fun toString(): String = "="
    }
}

data class BinaryExpressionContext(
    val left: ExpressionContext,
    val operator: Operator,
    val right: ExpressionContext,
    override val line: Int
) : ExpressionContext(line) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitBinaryExpression(this)
    override fun toString(): String = "$left$operator$right"
}