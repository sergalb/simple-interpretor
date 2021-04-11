package context

data class ConstantContext(val value: Int, override val line: Int) : ExpressionContext(line) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitConstant(this)
    override fun toString(): String = value.toString()
}