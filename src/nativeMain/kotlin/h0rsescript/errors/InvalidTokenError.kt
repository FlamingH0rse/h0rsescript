package me.flaming.h0rsescript.errors

class InvalidTokenError(char: Char) : H0Error() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "InvalidTokenError"
        super.message = "Invalid token '${char}'"
    }
}