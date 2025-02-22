package me.flaming.h0rsescript.errors

class ReferenceError(identifier: String): H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "ReferenceError"
        super.message = "Unexpected identifier '$identifier'"
    }
}