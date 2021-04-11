package context

data class ParameterVariableContext(
    val name: String,
    var value: Int? = null,
    override val line: Int
) : ExpressionContext(line) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitParameterVariable(this)
    override fun toString(): String = name
}