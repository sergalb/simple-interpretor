import context.*

import context.Operator.*
import exceptions.CompileTimeException
import exceptions.ProgramRuntimeException
import java.lang.IllegalStateException
import java.lang.RuntimeException

class InterpreterVisitor : Visitor<Int> {
    private val functions: MutableMap<String, FunctionDefinitionContext> = HashMap()

    override fun visitProgram(programContext: ProgramContext): Int {
        for (function in programContext.functions) {
            addFunction(function)
        }
        return programContext.expressionContext.accept(this)

    }


    private fun addFunction(functionDefinitionContext: FunctionDefinitionContext) {
        val old = functions.put(functionDefinitionContext.name, functionDefinitionContext)
        if (old != null) {
            throw CompileTimeException("DUPLICATE FUNCTION NAME", old.name, old.line)
        }
    }

    override fun visitFunctionCall(callContext: CallContext): Int {
        val function = functions[callContext.functionName]
            ?: throw CompileTimeException("FUNCTION NOT FOUND", callContext.functionName, callContext.line)
        val oldValues = function.parameters.map { it.value }
        if (function.parameters.size != callContext.arguments.size) throw CompileTimeException(
            "ARGUMENT NUMBER MISMATCH",
            callContext.functionName,
            callContext.line
        )
        for ((index, parameter) in function.parameters.withIndex()) {
            parameter.value = callContext.arguments[index].accept(this)
        }
        val res = function.expressionContext.accept(this)
        function.parameters.forEachIndexed { i, it -> if (oldValues[i] != null) it.value = oldValues[i] }
        return res
    }

    override fun visitIf(ifContext: IfContext): Int {
        val conditionRes = ifContext.condition.accept(this)
        return if (conditionRes == 1) {
            ifContext.thenExpression.accept(this)
        } else {
            ifContext.elseExpressionContext.accept(this)
        }
    }

    override fun visitBinaryExpression(binaryExpressionContext: BinaryExpressionContext): Int {
        val left = binaryExpressionContext.left.accept(this)
        val right = binaryExpressionContext.right.accept(this)
        return when (binaryExpressionContext.operator) {
            PLUS -> left + right
            MINUS -> left - right
            MUL -> left * right
            DIV -> {
                if (right == 0) throw ProgramRuntimeException(binaryExpressionContext)
                left / right
            }
            MOD -> {
                if (right == 0) throw ProgramRuntimeException(binaryExpressionContext)
                left % right
            }
            LESS -> if (left < right) 1 else 0
            GREATER -> if (left > right) 1 else 0
            EQUAL -> if (left == right) 1 else 0
        }
    }

    override fun visitConstant(constantContext: ConstantContext): Int = constantContext.value


    override fun visitParameterVariable(parameterVariableContext: ParameterVariableContext): Int =
        parameterVariableContext.value
            ?: throw ProgramRuntimeException(parameterVariableContext)

    override fun visitFunctionDefinition(functionDefinitionContext: FunctionDefinitionContext): Int = -1

}
