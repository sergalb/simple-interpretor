package context

data class FunctionDefinitionContext(
    val name: String,
    val parameters: List<ParameterVariableContext>,
    val expressionContext: ExpressionContext,
    val line: Int
) : Context {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitFunctionDefinition(this)

    override fun toString(): String = "$name(${parameters.joinToString(", ")})={$expressionContext}"

}