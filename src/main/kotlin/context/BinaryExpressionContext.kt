package context

enum class Operator() {
    PLUS, MINUS, MUL, DIV, MOD, LESS, GREATER, EQUAL
}
class BinaryExpressionContext(
    val left: ExpressionContext,
    val operator: Operator,
    val right: ExpressionContext
) : ExpressionContext() {
}