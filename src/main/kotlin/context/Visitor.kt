package context

interface Visitor<T> {
    fun visitProgram(programContext: ProgramContext): T
    fun visitFunction(functionContext: FunctionContext): T
    fun visitExpression(expressionContext: ExpressionContext): T
    fun visitConstant(constantContext: ConstantContext): T

}