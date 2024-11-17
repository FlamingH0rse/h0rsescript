package me.flaming.h0rsescript.error

import me.flaming.h0rsescript.Token

class InvalidTokenError(char: Char, line: Int?, column: Int?) : HSError() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "InvalidTokenError"
        super.message = "Invalid token '${token.value}'"
        super.line = line
        super.column = column
    }
}