package parser

import context.*
import exceptions.CompileTimeException
import exceptions.SyntaxException
import lexer.Token
import lexer.Type
import lexer.Type.*


class Parser(private val tokens: List<Token>) {
    private var current = 0
    private val parameterVariables: MutableMap<String, MutableMap<String, ParameterVariableContext>> = HashMap()
    private var curFunctionName = ""
    fun parseProgram(): ProgramContext {
        val res = ProgramContext(
            functionsDefinition(),
            expression()
        )
        checkCurrentType(EOF)
        return res
    }


    fun functionsDefinition(): List<FunctionDefinitionContext> {
        val token = tokens[current]
        return when (token.type) {
            IDENTIFIER -> {
                curFunctionName = token.value as String
                next()
                checkCurrentType(LPAREN)
                if (tokens[current + 1].type != IDENTIFIER) {
                    current--
                    return emptyList()
                }
                if (parameterVariables.containsKey(curFunctionName)) throw CompileTimeException("DUPLICATE FUNCTION", curFunctionName, token.line)
                next()
                val params = parameters()
                checkNextTypes(RPAREN, EQUAL, LBRACE)
                val function =
                    FunctionDefinitionContext(
                        curFunctionName,
                        params,
                        expression(),
                        token.line
                    )
                checkNextTypes(RBRACE, EOL)
                functionsDefinition().plusElement(function)
            }
            else -> emptyList()
        }
    }

    fun parameters(): List<ParameterVariableContext> {
        var token = next()
        val res = mutableListOf<ParameterVariableContext>()
        while (true) {
            if (token.type != IDENTIFIER) throw SyntaxException()
            val name = token.value as String
            val variableContext = ParameterVariableContext(name, line = token.line)
            val function = parameterVariables[curFunctionName]
            if (function != null) {
                val old = function.putIfAbsent(name, variableContext)
                if (old != null) {
                    throw CompileTimeException("DUPLICATE PARAMETER NAME", old.name, old.line)
                }
            } else {
                parameterVariables[curFunctionName] = mutableMapOf(Pair(name, variableContext))
            }
            res.add(variableContext)
            if (tokens[current].type != COMMA) {
                break
            }
            next()
            token = next()
        }
        return res
    }


    fun expression(): ExpressionContext {
        val token = tokens[current]
        return when (token.type) {
            IDENTIFIER -> call()
            NUMBER, MINUS -> unary()
            LPAREN -> {
                next()
                val res = addition()
                checkNextTypes(RPAREN)
                res
            }
            LBRACKET -> {
                next()
                val res = ifExpression()
                res
            }
            else -> throw SyntaxException()
        }

    }

    fun ifExpression(): IfContext {
        val condition = expression()
        checkNextTypes(RBRACKET, QUESTION, LBRACE)
        val thenExpression = expression()
        checkNextTypes(RBRACE, COLON, LBRACE)
        val elseExpression = expression()
        checkNextTypes(RBRACE)
        return IfContext(condition, thenExpression, elseExpression, condition.line)
    }

    fun call(): ExpressionContext {
        val token = next()
        val name = token.value as String
        return if (tokens[current].type != LPAREN) {
            parameterVariables[curFunctionName]?.get(name)
                ?: throw CompileTimeException("PARAMETER NOT FOUND", name, token.line)
        } else {
            next()
            val arguments = argumentList()
            val function = parameterVariables[name]
            if (function != null && function.size != arguments.size) throw CompileTimeException(
                "ARGUMENT NUMBER MISMATCH",
                name,
                token.line
            )
            val res = CallContext(name, arguments, token.line)
            checkNextTypes(RPAREN)
            res
        }
    }

    fun argumentList(): List<ExpressionContext> {
        val res = mutableListOf<ExpressionContext>()
        while (true) {
            res.add(expression())
            if (tokens[current].type != COMMA) {
                break
            }
            next()
        }
        return res
    }

    fun binaryExpression(nextLevel: Parser.() -> ExpressionContext, vararg operators: Type): ExpressionContext {
        var left = nextLevel()
        var operator = tokens[current].type
        while (operators.any { it == operator }) {
            next()
            val right = nextLevel()
            left = BinaryExpressionContext(left, Operator.valueOf(operator.name), right, left.line)
            operator = tokens[current].type
        }
        return left
    }

    fun addition(): ExpressionContext =
        binaryExpression({ multiplication() }, PLUS, MINUS)


    fun multiplication(): ExpressionContext =
        binaryExpression({ mod() }, MUL, DIV)


    fun mod(): ExpressionContext {
        return binaryExpression({ comparison() }, MOD)
    }

    private fun comparison(): ExpressionContext =
        binaryExpression({ unary() }, LESS, GREATER, EQUAL)


    fun unary(): ExpressionContext {
        var token = tokens[current]
        return when (token.type) {
            NUMBER -> {
                next()
                ConstantContext(token.value as Int, token.line)
            }
            MINUS -> {
                next()
                token = next()
                if (token.type != NUMBER) throw SyntaxException()
                return ConstantContext(-(token.value as Int), token.line)
            }
            else -> expression()
        }
    }

    private fun checkCurrentType(type: Type) {
        if (tokens[current].type != type) throw SyntaxException()
    }

    private fun checkNextTypes(vararg types: Type) {
        for (type in types)
            if (next().type != type) {
                throw SyntaxException()
            }
    }

    private fun next(): Token {
        if (tokens[current].type != EOF) {
            return tokens[current++]
        } else {
            throw SyntaxException()
        }
    }
}
