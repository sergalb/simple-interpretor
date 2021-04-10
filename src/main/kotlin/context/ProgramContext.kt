package context

class ProgramContext(val functions: List<FunctionContext>, val expressionContext: ExpressionContext): Context {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitProgram(this)
}