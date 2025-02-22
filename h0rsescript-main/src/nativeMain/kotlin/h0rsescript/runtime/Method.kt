package me.flaming.h0rsescript.runtime

import kotlin.reflect.KClass

// Enter parameterType as H0Type::class if your argument accepts Any? type
class Method(vararg val parameterTypes: KClass<out H0Type>, val runnable: (List<H0Type>) -> Any?) {
    fun execute(arguments: List<H0Type>): Any? = runnable.invoke(arguments)
}