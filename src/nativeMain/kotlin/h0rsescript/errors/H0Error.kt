package me.flaming.h0rsescript.errors

abstract class H0Error(
    var type: ErrorType = ErrorType.UNKNOWN,
    var name: String = "",
    var message: String = "",
    var line: Int? = null,
    var column: Int? = null
) {
    fun getMessage(): String {
        val location = if (line != null && column != null) "at line $line column $column" else ""

        return "${type}_ERROR: $name ${location}\n$message"
    }

    enum class ErrorType {
        SYNTAX, RUNTIME, UNKNOWN
    }
}