package context

class IfContext(
    val condition: ExpressionContext,
    val thenExpression: ExpressionContext,
    val elseExpressionContext: ExpressionContext
) : ExpressionContext()