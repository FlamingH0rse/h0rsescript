package me.flaming.h0rsescript.error

class IllegalAssignmentError(variableName: String): HSError() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "IllegalAssignmentError"
        super.message = "Cannot assign value to '$variableName'"
    }
}