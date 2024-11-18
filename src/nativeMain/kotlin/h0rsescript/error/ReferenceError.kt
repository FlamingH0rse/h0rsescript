package me.flaming.h0rsescript.error

class ReferenceError(identifier: String): HSError() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "ReferenceError"
        super.message = "Unexpected identifier '$identifier'"
    }
}