package me.flaming.h0rsescript.hs

import me.flaming.h0rsescript.ErrorHandler
import me.flaming.h0rsescript.error.TypeError

abstract class Namespace {
    abstract val methods: Map<String, Method>

    fun hasMethod(name: String): Boolean = name in methods
    fun executeMethod(name: String, arguments: List<HSType>): HSType {
        val method = methods[name]!!
        val parametersMap = method.parameterTypes.zip(arguments)
        // Check if parameters are the same type
        for ((type, parameter) in parametersMap) {
            if (!type.isInstance(parameter)) {
                // Throw TypeError
                ErrorHandler.report(TypeError(name, parameter::class, type))
            }
        }

        val functionOutput = method.execute(arguments)
        return HSType.from(functionOutput)
    }
}