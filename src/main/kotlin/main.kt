import lexer.Lexer
import parser.Parser

fun main() {
    val lexer = Lexer("[((10+20)>(20+10))]?{1}:{0}")
    val tokens = lexer.scan()
    val parser = Parser(tokens)
    val program = parser.parseProgram()
    println("done")


}