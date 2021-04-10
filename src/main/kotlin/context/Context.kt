package context


interface Context{
    fun <T> accept(visitor: Visitor<T>): T
}
