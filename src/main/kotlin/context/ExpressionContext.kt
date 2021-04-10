package context

open class ExpressionContext: Context {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitExpression(this)
}