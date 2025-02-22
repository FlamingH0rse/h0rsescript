package me.flaming.h0rsescript.errors

class InvalidTokenError(char: Char, line: Int?, column: Int?) : H0Error() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "InvalidTokenError"
        super.message = "Invalid token '${char}'"
        super.line = line
        super.column = column
    }
}