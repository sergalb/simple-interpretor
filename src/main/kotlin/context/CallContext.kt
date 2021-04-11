package context

data class CallContext(val functionName: String,
                  val arguments: List<ExpressionContext>,
                  override val line: Int) : ExpressionContext(line) {
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visitFunctionCall(this)
    override fun toString(): String = "$functionName(${arguments.joinToString(", ")})"
}