package context

data class ProgramContext(val functions: List<FunctionDefinitionContext>, val expressionContext: ExpressionContext) :
    Context {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitProgram(this)
    override fun toString(): String =
        functions.joinToString("\n") +
                "${if (functions.isEmpty()) "\n" else ""}$expressionContext"
}