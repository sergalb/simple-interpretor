import context.*
import context.Operator.*
import exceptions.CompileTimeException
import exceptions.SyntaxException
import lexer.Lexer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser

class ParserTest {
    private fun <T> provideEqualsTests(expected: T, input: String, part: Parser.() -> T) {
        val tokens = Lexer(input).scan()
        val parser = Parser(tokens)
        Assertions.assertEquals(expected, parser.part())
    }

    private fun <T> provideThrowSyntaxExceptionTests(input: String, part: Parser.() -> T) =
        provideThrowTests(SyntaxException::class.java, input, part)

    private fun <T> provideThrowCompileTimeExceptionTests(input: String, part: Parser.() -> T) =
        provideThrowTests(CompileTimeException::class.java, input, part)


    private fun <T, E : Throwable> provideThrowTests(exception: Class<E>, input: String, part: Parser.() -> T) {
        val tokens = Lexer(input).scan()
        val parser = Parser(tokens)
        Assertions.assertThrows(exception) { parser.part() }
    }


    @Test
    fun singleNumber() {
        provideEqualsTests(ConstantContext(1, 1), "1", Parser::unary)
    }

    @Test
    fun singleNumberAsExpression() {
        provideEqualsTests(ConstantContext(1, 1), "1", Parser::expression)
    }

    @Test
    fun negateNumber() {
        provideEqualsTests(ConstantContext(-1, 1), "-1", Parser::unary)
    }

    @Test
    fun sum() {
        provideEqualsTests(
            BinaryExpressionContext(ConstantContext(2, 1), PLUS, ConstantContext(3, 1), 1),
            "(2+3)",
            Parser::expression
        )
    }

    @Test
    fun sumWithNegate() {
        provideEqualsTests(
            BinaryExpressionContext(ConstantContext(2, 1), PLUS, ConstantContext(-3, 1), 1),
            "(2+-3)",
            Parser::expression
        )
    }

    @Test
    fun complexExpression() {
        provideEqualsTests(
            BinaryExpressionContext(
                BinaryExpressionContext(
                    BinaryExpressionContext(
                        ConstantContext(
                            1,
                            1
                        ), PLUS, ConstantContext(2, 1), 1
                    ), DIV, ConstantContext(3, 1), 1
                ),
                MUL,
                BinaryExpressionContext(
                    BinaryExpressionContext(ConstantContext(4, 1), MOD, ConstantContext(5, 1), 1),
                    LESS,
                    BinaryExpressionContext(ConstantContext(6, 1), EQUAL, ConstantContext(7, 1), 1),
                    1
                ),
                1
            ), "(((1+2)/3)*((4%5)<(6=7)))", Parser::expression
        )
    }

    @Test
    fun functionDefinition() {
        provideEqualsTests(
            listOf(
                FunctionDefinitionContext(
                    "f",
                    listOf(ParameterVariableContext("x", null, 1), ParameterVariableContext("y", null, 1)),
                    ConstantContext(1, 1),
                    1
                )
            ), "f(x,y)={1}\n", Parser::functionsDefinition
        )
    }

    @Test
    fun functionCall() {
        provideEqualsTests(
            CallContext(functionName = "f", arguments = listOf(ConstantContext(value = 1, line = 1)), line = 1),
            "f(1)",
            Parser::call
        )
    }

    @Test
    fun ifExpression() {
        provideEqualsTests(
            IfContext(
                condition = ConstantContext(value = 1, line = 1),
                thenExpression = ConstantContext(value = 2, line = 1),
                elseExpressionContext = ConstantContext(value = 3, line = 1),
                line = 1
            ),
            "[1]?{2}:{3}",
            Parser::expression
        )
    }

    @Test
    fun program() {
        provideEqualsTests(
            ProgramContext(
                functions = listOf(
                    FunctionDefinitionContext(
                        name = "q",
                        parameters = listOf(ParameterVariableContext(name = "z", value = null, line = 1)),
                        expressionContext = ParameterVariableContext(name = "z", value = null, line = 1),
                        line = 1
                    )
                ),
                expressionContext = CallContext(
                    functionName = "q",
                    arguments = listOf(ConstantContext(value = -1, line = 2)),
                    line = 2
                )
            ),
            "q(z)={z}\n" +
                    "q(-1)",
            Parser::parseProgram
        )
    }

    @Test
    fun expressionWithoutParenthesesShouldFail() {
        provideThrowSyntaxExceptionTests("2+2", Parser::parseProgram)
    }

    @Test
    fun doubleNegateShouldFail() {
        provideThrowSyntaxExceptionTests("--1", Parser::expression)
    }

    @Test
    fun ifWithoutElsePartShouldFail() {
        provideThrowSyntaxExceptionTests("[1]?{2}", Parser::expression)
    }

    @Test
    fun incorrectFunctionDeclaration() {
        provideThrowSyntaxExceptionTests("f(x){1}", Parser::functionsDefinition)
    }

    @Test
    fun functionWithoutParametersShouldFail() {
        provideThrowSyntaxExceptionTests("f()={1}\n1", Parser::parseProgram)
    }

    @Test
    fun functionCallWithoutArgumentShouldFail() {
        provideThrowSyntaxExceptionTests("f()", Parser::call)
    }

    @Test
    fun lessArgumentsMismatch() {
        provideThrowCompileTimeExceptionTests("f(x,y)={x}\nf(1)", Parser::parseProgram)
    }

    @Test
    fun moreArgumentsMismatch() {
        provideThrowCompileTimeExceptionTests("f(x,y)={x}\nf(1,2,3)", Parser::parseProgram)
    }

    @Test
    fun duplicateFunctions() {
        provideThrowCompileTimeExceptionTests("f(x)={1}\nf(z)={2}\n", Parser::functionsDefinition)
    }

    @Test
    fun duplicateParameterName() {
        provideThrowCompileTimeExceptionTests("f(x,x)={1}\n", Parser::functionsDefinition)
    }

    @Test
    fun unknownParameter() {
        provideThrowCompileTimeExceptionTests("f(x)={z}\n", Parser::functionsDefinition)
    }


}