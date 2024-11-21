package me.flaming.h0rsescript.error

class ArithmeticError(message: String): HSError() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "ArithmeticError"
        super.message = "Couldn't execute math operation\n$message"
    }
}