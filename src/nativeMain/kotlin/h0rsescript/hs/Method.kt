package me.flaming.h0rsescript.hs

import me.flaming.h0rsescript.ErrorHandler
import me.flaming.h0rsescript.error.TypeError
import kotlin.reflect.KClass

class Method(vararg val parameterTypes: KClass<out HSType>, val runnable: (List<HSType>) -> HSType?) {
    fun execute(arguments: List<HSType>): HSType? = runnable.invoke(arguments)
}