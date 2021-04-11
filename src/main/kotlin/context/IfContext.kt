package context

data class IfContext(
    val condition: ExpressionContext,
    val thenExpression: ExpressionContext,
    val elseExpressionContext: ExpressionContext,
    override val line: Int
) : ExpressionContext(line) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitIf(this)
    override fun toString(): String = "[$condition]?($thenExpression):($elseExpressionContext)"
}