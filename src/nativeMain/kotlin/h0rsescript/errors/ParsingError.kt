package me.flaming.h0rsescript.errors

import me.flaming.h0rsescript.core.H0Type
import kotlin.reflect.KClass

class ParsingError(value: Any, to: KClass<out H0Type>): H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "ParsingError"
        super.message = "Error parsing '$value' to ${to.simpleName}"
    }
}