package me.flaming.h0rsescript.error

class InvalidTokenError(char: Char, line: Int?, column: Int?) : HSError() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "InvalidTokenError"
        super.message = "Invalid token '${char}'"
        super.line = line
        super.column = column
    }
}