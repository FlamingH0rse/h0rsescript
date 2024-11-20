package me.flaming.h0rsescript.tokens

import me.flaming.h0rsescript.ErrorHandler
import me.flaming.h0rsescript.error.InvalidTokenError

object Tokenizer {
    private const val keywordPrefixPattern = """\$"""
    private val keywords = listOf("define", "end", "parameters", "return", "mode", "include")
    private val symbols = listOf("->", "<->", ">", "<-", "<")

    // Follows priority of matching
    private val tokenPatterns = mapOf(
        TokenType.KEYWORD to keywords.joinToString("|") { k -> "$keywordPrefixPattern$k" },
        TokenType.BOOLEAN to """true|false|TRUE|FALSE""",
        TokenType.NUMBER to """\d+(\.\d+)*""",
        TokenType.STRING to """"([^"\\]|\\.)*"""",

        TokenType.IDENTIFIER to """[a-zA-Z_][a-zA-Z0-9_]+""",
        TokenType.ASSIGNMENT_OPERATOR to symbols.joinToString("|"),


        TokenType.OPEN_BRACKET to """\[""",
        TokenType.CLOSE_BRACKET to """]""",

        TokenType.COMMA to """,""",
//        TokenType.NEWLINE to """\n""",
        TokenType.WHITESPACE to """\s+""",
        TokenType.COMMENT to """#.*"""
    )

    // Create tokens
    fun tokenize(input: String): MutableList<Token> {
        val tokens: MutableList<Token> = mutableListOf()

        var line = 1
        var column = 0
        var position = 0

        while (position < input.length) {
            var matched = false

            // Check for all pattern matches
            for ((type, pattern) in tokenPatterns) {
                val regex = Regex(pattern)
                val match = regex.find(input, position)

                // Check if match exists at current position
                if (match != null && match.range.first == position) {
                    // Add token to list
                    val value = match.value
                    val matchToken = Token(type, value, Pair(line, column), position)
                    tokens.add(matchToken)

                    // Update line position
                    if (value.contains('\n')) {
                        val newLines = Regex("\n").findAll(value)
                        val lastNewLineIndex = newLines.last().range.last

                        line += newLines.count()
                        column = value.substring(lastNewLineIndex).length
                    }
                    // Update absolute position
                    position += match.value.length

                    // Move to next position
                    matched = true
                    break
                }
            }

            if (!matched) {
                // Throw InvalidTokenError
                ErrorHandler.report(InvalidTokenError(input[position], line, column))
            }
        }

        return tokens
    }
}