import lexer.Lexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser
import java.lang.RuntimeException

class InterpreterTest {
    private fun provideEqualsTests(expected: Int, input: String) {
        val tokens = Lexer(input).scan()
        val program = Parser(tokens).parseProgram()
        val interpreterVisitor = InterpreterVisitor()
        Assertions.assertEquals(expected, interpreterVisitor.visitProgram(program))
    }

    private fun provideRuntimeExceptionThrows(input: String) {
        val tokens = Lexer(input).scan()
        val program = Parser(tokens).parseProgram()
        val interpreterVisitor = InterpreterVisitor()
        Assertions.assertThrows(RuntimeException::class.java) { interpreterVisitor.visitProgram(program) }
    }

    @Test
    fun singleNumber() {
        provideEqualsTests(1, "1")
    }

    @Test
    fun negateNumber() {
        provideEqualsTests(-1, "-1")
    }

    @Test
    fun simpleExpression() {
        provideEqualsTests(4, "(2+2)")
    }

    @Test
    fun complexExpression() {
        provideEqualsTests(3, "((((1+2)/3)*((4%5)>(6=7)))*3)")
    }

    @Test
    fun constantFunction() {
        provideEqualsTests(1, "f(x)={1}\nf(10)")
    }

    @Test
    fun sumFunction() {
        provideEqualsTests(3, "f(x,y)={(x+y)}\nf(1,2)")
    }

    @Test
    fun ifExpression() {
        provideEqualsTests(17, "[0]?{7}:{17}")
    }

    @Test
    fun recursion() {
        provideEqualsTests(15, "f(x)={[(x>1)]?{(f((x-1))+x)}:{x}}\nf(5)")
    }

    @Test
    fun fewFunctions() {
        provideEqualsTests(
            19, "f(x,y,z)={(g(x,y)+z)}\n" +
                    "g(x,y)={(z(x)*y)}\n" +
                    "z(x)={(x/3)}\n"
                    + "f(6,7,5)"
        )
    }

    @Test
    fun divisionByZero() {
        provideRuntimeExceptionThrows("(1/0)")
    }

    @Test
    fun unknownFunction() {
        provideRuntimeExceptionThrows("f(1)")
    }

    @Test
    fun unknownFunctionWhenAnotherExist() {
        provideRuntimeExceptionThrows("f(x)={1}\ng(1)")
    }

    @Test
    fun modOnZero() {
        provideRuntimeExceptionThrows("(1%0)")
    }

}