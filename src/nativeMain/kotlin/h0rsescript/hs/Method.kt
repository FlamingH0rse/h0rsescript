package me.flaming.h0rsescript.hs

import kotlin.reflect.KClass

// Enter parameterType as HSType.NULL::class if your argument accepts nullable type
class Method(vararg val parameterTypes: KClass<out HSType>, val runnable: (List<HSType>) -> Any?) {
    fun execute(arguments: List<HSType>): Any? = runnable.invoke(arguments)
}