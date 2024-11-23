package me.flaming.h0rsescript.errors

class IllegalAssignmentError(variableName: String): H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "IllegalAssignmentError"
        super.message = "Cannot assign value to '$variableName'"
    }
}