package me.flaming.h0rsescript.runtime

import me.flaming.h0rsescript.errors.TypeError

abstract class Namespace {
    abstract val methods: Map<String, Method>

    fun hasMethod(name: String): Boolean = name in methods
    fun executeMethod(name: String, arguments: List<H0Type>): H0Type {
        val method = methods[name]!!

        val parametersMap = method.parameterTypes.zip(arguments).toMutableList()
        // Put NULL for empty parameters
        parametersMap.addAll(method.parameterTypes.drop(arguments.size).map {a -> Pair(a, H0Type.NULL())})

        // Check if parameters are the same type
        for ((type, parameter) in parametersMap) {
            if (!type.isInstance(parameter)) {
                // Throw TypeError
                ErrorHandler.report(TypeError(parameter.toString(), parameter::class, type))
            }
        }

        val functionOutput = method.execute(parametersMap.map { (_,b) -> b })
        return H0Type.from(functionOutput)
    }
}