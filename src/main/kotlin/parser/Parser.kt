package parser

import context.*
import lexer.Token
import lexer.Type
import lexer.Type.*


class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parseProgram(): ProgramContext {
        val res = ProgramContext(
            functionsDefinition(),
            expression()
        )
        if (tokens[current].type != EOF) throw SyntaxException()
        return res
    }


    private fun functionsDefinition(): List<FunctionContext> {
        val token = tokens[current]
        return when (token.type) {
            IDENTIFIER -> {
//                checkNextTypes(LPAREN)
                next()
                if (tokens[current].type != LPAREN) {
                    throw SyntaxException()
                }
                if (tokens[current + 1].type != IDENTIFIER) {
                    current--
                    return emptyList()
                }
                next()
                val params = parameters()
                checkNextTypes(RPAREN, EQUAL, LBRACE)
                val function =
                    FunctionContext(
                        token.value as String,
                        params,
                        expression()
                    )
                checkNextTypes(RBRACE, EOL)
                functionsDefinition().plusElement(function)
            }
            else -> emptyList()
        }
    }

    private fun parameters(): List<IdentifierContext> {
        var token = next()
        val res = mutableListOf<IdentifierContext>()
        while (true) {
            if (token.type != IDENTIFIER) throw SyntaxException()
            res.add(IdentifierContext((token.value) as String))
            if (next().type != COMMA) {
                break
            }
            token = tokens[current]
        }
        return res
    }


    private fun expression(): ExpressionContext {
        val token = tokens[current]
        return when (token.type) {
            IDENTIFIER -> call()
            NUMBER -> ConstantContext(next().value as Int)
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

    private fun ifExpression(): IfContext {
        val condition = expression()
        checkNextTypes(RBRACKET, QUESTION, LBRACE)
        val thenExpression = expression()
        checkNextTypes(RBRACE, COLON, LBRACE)
        val elseExpression = expression()
        checkNextTypes(RBRACE)
        return IfContext(condition, thenExpression, elseExpression)
    }

    private fun call(): ExpressionContext {
        val token = next()
        return if (tokens[current].type != LPAREN) {
            IdentifierContext(token.value as String)
        } else {
            next()
            val res = CallContext(token.value as String, argumentList())
            checkNextTypes(RPAREN)
            res
        }
    }

    private fun argumentList(): List<ExpressionContext> {
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

    private fun binaryExpression(nextLevel: Parser.() -> ExpressionContext, vararg operators: Type): ExpressionContext {
        var left = nextLevel()
        var operator = tokens[current].type
        while (operators.any { it == operator }) {
            next()
            val right = nextLevel()
            left = BinaryExpressionContext(left, Operator.valueOf(operator.name), right)
            operator = tokens[current].type
        }
        return left
    }

    private fun addition(): ExpressionContext =
        binaryExpression({ multiplication() }, PLUS, MINUS)


    private fun multiplication(): ExpressionContext =
        binaryExpression({ mod() }, MUL, DIV)


    private fun mod(): ExpressionContext {
        return binaryExpression({ comparison() }, MOD)
    }

    private fun comparison(): ExpressionContext =
        binaryExpression({ unary() }, LESS, GREATER, EQUAL)


    private fun unary(): ExpressionContext {
        var token = tokens[current]
        return when (token.type) {
            NUMBER -> {
                next()
                ConstantContext(token.value as Int)
            }
            MINUS -> {
                token = next()
                if (token.type != NUMBER) throw SyntaxException()
                return ConstantContext(-(token.value as Int))
            }
            else -> expression()
        }
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
