package context

class CallContext(val functionName: String, val arguments: List<ExpressionContext>): ExpressionContext() {
}