package me.flaming.h0rsescript.errors

abstract class H0Error(

    var type: ErrorType = ErrorType.UNKNOWN,
    var name: String = "",
    var message: String = "",

    ) {
    fun getMessage(): String {

        return "${type}_ERROR: $name\n$message"

    }

    enum class ErrorType {
        SYNTAX, RUNTIME, UNKNOWN
    }
}