package lexer

import lexer.Type.*
import java.util.regex.PatternSyntaxException
import kotlin.text.Regex.Companion.escape

class Lexer(private val input: String) {
    private var cur = 0
    private var curLine = 0

    private val regexes = mutableMapOf(
        "[a-zA-Z_]+" to IDENTIFIER,
        "0|[1-9]([0-9]+)?" to NUMBER,
        ":" to COLON,
        "-" to MINUS,
        "*" to MUL,
        "/" to DIV,
        "%" to MOD,
        ">" to GREATER,
        "<" to LESS,
        "=" to EQUAL,
        "," to COMMA,
        "{" to LBRACE,
        "}" to RBRACE,
        "(" to LPAREN,
        ")" to RPAREN,
        "[" to LBRACKET,
        "]" to RBRACKET,
        "?" to QUESTION,
        "+" to PLUS,
        "\n" to EOL
    )


    fun scan(): List<Token> {
        val res: MutableList<Token> = ArrayList()
        while (cur < input.length) {
            var maxPref = 0
            var bestMatch: MatchResult? = null
            var bestToken: Token? = null
            for ((regexpString, tokenType) in regexes) {
                val regexp: Regex =
                    try {
                        Regex(regexpString)
                    } catch (e: PatternSyntaxException) {
                        Regex(escape(regexpString))
                    }
                val match = regexp.find(input, cur)
                if (match !== null) {
                    if ((match.range.first == cur) && match.range.count() > maxPref) {
                        maxPref = match.range.count()
                        bestMatch = match
                        bestToken = Token(tokenType, line = curLine)
                    }
                }
            }
            if (bestMatch !== null) {
                cur += maxPref
                val value = bestMatch.value
                bestToken!!.value =
                    when (bestToken.type) {
                        IDENTIFIER -> value
                        NUMBER -> value.toInt()
                        else -> null
                    }

                res.add(bestToken)
                if (bestToken.type == EOL) curLine++
            } else {
                throw IllegalArgumentException("SYNTAX ERROR")
            }
        }
        res.add(Token(EOF, curLine))
        return res
    }

}

