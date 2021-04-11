import exceptions.SyntaxException
import lexer.Lexer
import lexer.Token
import lexer.Type.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LexerTest {

    private fun provideEqualsTest(expectedTokens: List<Token>, input: String) =
        Assertions.assertEquals(
            expectedTokens.plus(Token(EOF, expectedTokens.last().line)),
            Lexer(input).scan()
        )

    private fun provideThrowTest(input: String) =
        Assertions.assertThrows(SyntaxException::class.java, Lexer(input)::scan)

    @Test
    fun singleNumber() {
        provideEqualsTest(listOf(Token(NUMBER, 1, 1)), "1")
    }

    @Test
    fun negateNumber() {
        provideEqualsTest(listOf(Token(MINUS, 1), Token(NUMBER, 1, 1)), "-1")
    }

    @Test
    fun identifier() {
        provideEqualsTest(listOf(Token(IDENTIFIER, 1, "a")), "a")
    }

    @Test
    fun complexIdentifier() {
        provideEqualsTest(listOf(Token(IDENTIFIER, 1, "a_abB_Cc")), "a_abB_Cc")
    }

    @Test
    fun sum() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 2), Token(PLUS, 1), Token(NUMBER, 1, 2)),
            "2+2"
        )
    }

    @Test
    fun subtraction() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 0), Token(MINUS, 1), Token(NUMBER, 1, 3)),
            "0-3"
        )
    }

    @Test
    fun multiplication() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 5), Token(MUL, 1), Token(NUMBER, 1, 7)),
            "5*7"
        )
    }

    @Test
    fun division() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 10), Token(DIV, 1), Token(NUMBER, 1, 4)),
            "10/4"
        )
    }

    @Test
    fun mod() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 17), Token(MOD, 1), Token(NUMBER, 1, 7)),
            "17%7"
        )
    }

    @Test
    fun greater() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 0), Token(GREATER, 1), Token(NUMBER, 1, 0)),
            "0>0"
        )
    }

    @Test
    fun equal() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 0), Token(EQUAL, 1), Token(NUMBER, 1, 0)),
            "0=0"
        )
    }

    @Test
    fun less() {
        provideEqualsTest(
            listOf(Token(NUMBER, 1, 0), Token(LESS, 1), Token(NUMBER, 1, 0)),
            "0<0"
        )
    }

    @Test
    fun complexExpression() {
        provideEqualsTest(
            listOf(
                Token(LPAREN, 1),
                Token(LPAREN, 1),
                Token(LPAREN, 1),
                Token(NUMBER, 1, 1),
                Token(PLUS, 1),
                Token(NUMBER, 1, 2),
                Token(RPAREN, 1),
                Token(DIV, 1),
                Token(NUMBER, 1, 3),
                Token(RPAREN, 1),
                Token(MUL, 1),
                Token(LPAREN, 1),
                Token(LPAREN, 1),
                Token(NUMBER, 1, 4),
                Token(MOD, 1),
                Token(NUMBER, 1, 5),
                Token(RPAREN, 1),
                Token(LESS, 1),
                Token(LPAREN, 1),
                Token(NUMBER, 1, 6),
                Token(EQUAL, 1),
                Token(NUMBER, 1, 7),
                Token(RPAREN, 1),
                Token(RPAREN, 1),
                Token(RPAREN, 1)
            ),
            "(((1+2)/3)*((4%5)<(6=7)))"
        )
    }

    @Test
    fun functionDefinition() {
        provideEqualsTest(
            listOf(
                Token(IDENTIFIER, 1, "f"),
                Token(LPAREN, 1),
                Token(IDENTIFIER, 1, "x"),
                Token(COMMA, 1),
                Token(IDENTIFIER, 1, "y"),
                Token(RPAREN, 1),
                Token(EQUAL, 1),
                Token(LBRACE, 1),
                Token(NUMBER, 1, 1),
                Token(RBRACE, 1)
            ), "f(x,y)={1}"
        )
    }

    @Test
    fun ifExpression() {
        provideEqualsTest(
            listOf(
                Token(LBRACKET, 1),
                Token(NUMBER, 1, 1),
                Token(RBRACKET, 1),
                Token(QUESTION, 1),
                Token(LBRACE, 1),
                Token(NUMBER, 1, 2),
                Token(RBRACE, 1),
                Token(COLON, 1),
                Token(LBRACE, 1),
                Token(NUMBER, 1, 3),
                Token(RBRACE, 1)
            ), "[1]?{2}:{3}"
        )
    }

    @Test
    fun multiline() {
        provideEqualsTest(
            listOf(
                Token(IDENTIFIER, 1, "f"),
                Token(LPAREN, 1),
                Token(IDENTIFIER, 1, "x"),
                Token(RPAREN, 1),
                Token(EQUAL, 1),
                Token(LBRACE, 1),
                Token(IDENTIFIER, 1, "x"),
                Token(RBRACE, 1),
                Token(EOL, 1),
                Token(IDENTIFIER, 2, "f"),
                Token(LPAREN, 2),
                Token(NUMBER, 2, 1),
                Token(RPAREN, 2)
            ), "f(x)={x}\nf(1)"
        )
    }

    @Test
    fun wrongExpression() {
        provideThrowTest("2^2")
    }
}