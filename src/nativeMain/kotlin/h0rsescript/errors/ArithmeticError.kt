package me.flaming.h0rsescript.errors

class ArithmeticError(message: String): H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "ArithmeticError"
        super.message = "Couldn't execute math operation\n$message"
    }
}