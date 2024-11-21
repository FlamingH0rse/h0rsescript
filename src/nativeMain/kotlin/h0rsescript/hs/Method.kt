package me.flaming.h0rsescript.hs

import kotlin.reflect.KClass

class Method(vararg val parameterTypes: KClass<out HSType>, val runnable: (List<HSType>) -> Any?) {
    fun execute(arguments: List<HSType>): Any? = runnable.invoke(arguments)
}