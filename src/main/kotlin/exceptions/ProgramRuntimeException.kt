package exceptions

import context.ExpressionContext

class ProgramRuntimeException(expressionContext: ExpressionContext) :
    RuntimeException("RUNTIME ERROR $expressionContext:${expressionContext.line}") {
}