package context

import lexer.Token

class FunctionContext(val name: String, val parameters: List<IdentifierContext>, val expressionContext: ExpressionContext): Context {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitFunction(this)
}