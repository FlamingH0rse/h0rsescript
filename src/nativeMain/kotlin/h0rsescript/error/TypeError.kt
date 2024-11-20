package me.flaming.h0rsescript.error

import me.flaming.h0rsescript.hs.HSType
import kotlin.reflect.KClass

class TypeError(name: String, type: KClass<out HSType>, expected: KClass<out HSType>): HSError() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "TypeError"
        super.message = "Invalid identifier '${name}' of type ${type.simpleName}, expected a ${expected.simpleName}"
    }
}