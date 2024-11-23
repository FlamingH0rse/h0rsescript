package me.flaming.h0rsescript.errors

import me.flaming.h0rsescript.core.H0Type
import kotlin.reflect.KClass

class TypeError(name: String, type: KClass<out H0Type>, expected: KClass<out H0Type>): H0Error() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "TypeError"
        super.message = "Invalid argument '${name}' of type ${type.simpleName}, expected a ${expected.simpleName}"
    }
}