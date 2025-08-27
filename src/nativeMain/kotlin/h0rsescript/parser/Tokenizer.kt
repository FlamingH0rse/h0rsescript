package me.flaming.h0rsescript.parser

import me.flaming.h0rsescript.errors.InvalidTokenError
import me.flaming.h0rsescript.runtime.ErrorHandler

object Tokenizer {

    var src = ""
    private var position = 0

    fun tokenize(input: String, clean: Boolean = false): List<Token> {
        src = input
        val tokens = mutableListOf<Token>()

        val consumeToken = { token: Token ->
            tokens.add(token)
            position += token.value.length
        }

        while (position < input.length) {
            val char = input[position]
            val startPos = position

            // Handle identifiers, and booleans
            if (char.isLetter() || char == '_') {
                val value = getIdentifier()
                var tokenType = if (value.contains('.')) TokenType.QUALIFIED_IDENTIFIER else TokenType.IDENTIFIER

                // Handle booleans
                if (value in Token.booleans) tokenType = TokenType.BOOLEAN

                tokens.add(Token(tokenType, value, position = startPos))
            }

            // Handle keywords
            else if (char == Token.KEYWORDPREFIX) {
                tokens.add(Token(TokenType.KEYWORD, getFromList(Token.keywords), position = startPos))
            }

            // Handle keywords
            else if (char == Token.TAGPREFIX) {
                tokens.add(Token(TokenType.TAG, getFromList(Token.tags), position = startPos))
            }

            // Handle assignment operators
            else if (isAssignmentOpChar()) {
                tokens.add(Token(TokenType.OPERATOR, getAssignmentOp(), position = startPos))
            }

            // Handle strings and numbers
            else if (char == '"') {
                tokens.add(Token(TokenType.STRING, getStringLiteral(), position = startPos))
            }
            else if (char == '-' || char.isDigit()) {
                tokens.add(Token(TokenType.NUMBER, getNumberLiteral(), position = startPos))
            }

            // Handle single character tokens like brackets and commas and whitespaces
            else if (char == '[') {
                consumeToken(Token(TokenType.OPEN_BRACKET, "[", position = startPos))
            } else if (char == ']') {
                consumeToken(Token(TokenType.CLOSE_BRACKET, "]", position = startPos))
            } else if (char == '{') {
                consumeToken(Token(TokenType.OPEN_CURLY, "{", position = startPos))
            } else if (char == '}') {
                consumeToken(Token(TokenType.CLOSE_CURLY, "}", position = startPos))
            } else if (char == ',') {
                consumeToken(Token(TokenType.COMMA, ",", position = startPos))
            } else if (char.isWhitespace()) {
                position++
            }

            // Comments
            else if (char == ';') {
                tokens.add(Token(TokenType.COMMENT, getComment(), position = startPos))
            }

            // Throw InvalidTokenError
            else {
                ErrorHandler.report(InvalidTokenError(src[position], getLineCol(position).first, getLineCol(position).second))
            }
        }
        if (clean) {
            tokens.removeAll { t -> t.type == TokenType.COMMENT }
        }
        return tokens
    }

    // Goofy ahh code
    private fun isAssignmentOpChar(): Boolean {
        val subStr = src.substring(position)
        return subStr.startsWith("<->") ||
                subStr.startsWith("->") ||
                subStr.startsWith("<-") ||
                subStr.startsWith(">") ||
                subStr.startsWith("<") ||
                subStr.startsWith("-")
    }
    private fun getAssignmentOp(): String {
        val subStr = src.substring(position)
        var operatorValue: String

        // Goofy ahh code v2
        if (subStr.startsWith("<->")) {
            operatorValue = "<->"
            position += 3
        } else if (subStr.startsWith("->")) {
            operatorValue = "->"
            position += 2
        } else if (subStr.startsWith("<-")) {
            operatorValue = "<-"
            position += 2
        } else if (subStr.startsWith(">")) {
            operatorValue = ">"
            position += 1
        } else if (subStr.startsWith("<")) {
            operatorValue = "<"
            position += 1
        } else if (subStr.startsWith("-")) {
            operatorValue = "-"
            position += 1
        } else {
            ErrorHandler.report(InvalidTokenError(src[position], getLineCol(position).first, getLineCol(position).second))
        }

        return operatorValue
    }

    private fun getStringLiteral(): String {
        val startPos = position
        var literalValue = ""

        // Start double quote "
        position++

        // Get all characters after opening ", until ending " or EOF
        while (!endOfSrc() && src[position] != '"') {
            literalValue += src[position]
            position++

            // Handle escape sequences
            if (src[position] == '\\') {
                literalValue += src[position++]
            }
        }

        // Ending double quote ", throw error if EOF
        if (endOfSrc()) {
            val err = InvalidTokenError(src[startPos], getLineCol(startPos).first, getLineCol(startPos).second)
            err.message += "\nUnterminated string literal"
            ErrorHandler.report(err)
        }
        position++

        return literalValue
    }

    // -?[0-9]+(\.[0-9]+)?
    private fun getNumberLiteral(): String {
        // Get first digit or negative symbol
        var literalValue = "${src[position]}"
        var isDecimalValid: Boolean? = null

        // Making decimal usage valid after 1 digit
        if (src[position].isDigit()) isDecimalValid = true

        position++

        // Get all numbers (and 1 decimal point)
        while (!endOfSrc() && (src[position].isDigit() || (src[position] == '.' && isDecimalValid == true))) {
            literalValue += src[position]

            // Making decimal usage valid after 1 digit
            if (isDecimalValid == null) isDecimalValid = true

            // Making decimal usage invalid after 1 use
            if (src[position] == '.') {
                isDecimalValid = false
            }

            position++
        }

        // Remove decimal if no number after it
        if (literalValue.endsWith('.')) {
            literalValue = literalValue.removeSuffix(".")
            position--
        }

        return literalValue
    }

    // [a-zA-Z_][a-zA-Z0-9_]*
    private fun getIdentifier(): String {
        // Get first letter or underscore
        var value = "${src[position]}"
        position++

        var endOfIdent = false
        while (!endOfIdent) {
            // Get current character, except .
            if (!endOfSrc() && isValidIdentChar(src[position])) {
                value += src[position]
                position++
            }
            // Get . only if it is followed by another character
            else if (!endOfSrc() && src[position] == '.' && (src.getOrNull(position + 1) != null && isValidIdentChar(src[position + 1]))) {
                value += src[position]
                position++
            }
            else endOfIdent = true
        }

        return value
    }

    private fun getFromList(list: List<String>): String {
        val startPos = position
        var str = ""

        // Get all until whitespace
        while (!endOfSrc() && !src[position].isWhitespace()) {
            str += src[position]
            position++
        }

        // Throw error if token is not in list
        if (str !in list) {
            val err = InvalidTokenError(src[startPos], getLineCol(startPos).first, getLineCol(startPos).second)
            err.message += "\nInvalid keyword '$str' used"
            err.message += "\nExpected one of the following:\n\t'${list.joinToString("\n\t")}'"
            ErrorHandler.report(err)
        }

        return str
    }

    private fun getComment(): String {
        var comment = ""
        while (endOfSrc() || src[position] != '\n') {
            comment += src[position]
            position++
        }
        return comment
    }

    private fun isValidIdentChar(char: Char) = char.isLetterOrDigit() || char == '_'

    private fun endOfSrc() = position >= src.length

    fun getLineCol(pos: Int): Pair<Int, Int> {
        val subStr = src.substring(0, pos)
        val lines = subStr.split('\n')

        val line = lines.size
        val column = lines.last().length + 1

        return Pair(line, column)
    }
}