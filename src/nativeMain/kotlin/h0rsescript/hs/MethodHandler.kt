package me.flaming.h0rsescript.hs

import me.flaming.h0rsescript.Interpreter

object MethodHandler {
    private val methods = mapOf<String, (List<Any?>) -> Any?>(
        "data" to { args -> args[0]}
    )
    fun exists(name: String): Boolean {
        return name in methods
    }

    fun execute(name: String, arguments: List<Any?>): Any? {
        println("Executing $name [ ${arguments.joinToString(",")} ]")
        if (exists(name)) return methods[name]!!(arguments)
        else {
            // Shouldn't happen
            return null
        }
    }
}