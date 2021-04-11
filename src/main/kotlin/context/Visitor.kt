package context

interface Visitor<T> {
    fun visitProgram(programContext: ProgramContext): T
    fun visitFunctionDefinition(functionDefinitionContext: FunctionDefinitionContext): T
    fun visitFunctionCall(callContext: CallContext): T
    fun visitIf(ifContext: IfContext): T
    fun visitBinaryExpression(binaryExpressionContext: BinaryExpressionContext): T
    fun visitConstant(constantContext: ConstantContext): T
    fun visitParameterVariable(parameterVariableContext: ParameterVariableContext): T
}