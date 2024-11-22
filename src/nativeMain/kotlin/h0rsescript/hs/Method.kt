package me.flaming.h0rsescript.hs

import kotlin.reflect.KClass

// Enter parameterType as HSType::class if your argument accepts Any? type
class Method(vararg val parameterTypes: KClass<out HSType>, val runnable: (List<HSType>) -> Any?) {
    fun execute(arguments: List<HSType>): Any? = runnable.invoke(arguments)
}