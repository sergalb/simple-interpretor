import lexer.Lexer
import parser.Parser
import java.io.File

fun main(args: Array<String>) {
    val file = File(args[0])
    val programSource = file.readText()
    try {
        val lexer = Lexer(programSource)
        val tokens = lexer.scan()
        val parser = Parser(tokens)
        val program = parser.parseProgram()
        val interpreterVisitor = InterpreterVisitor()
        val res = interpreterVisitor.visitProgram(program)
        println(res)
    } catch (e: RuntimeException) {
        println(e.message)
    }

}