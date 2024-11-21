package me.flaming.h0rsescript.hs

import me.flaming.h0rsescript.ErrorHandler
import me.flaming.h0rsescript.error.ReferenceError
import me.flaming.h0rsescript.hs.namespaces.*

class MethodHandler(namespaces: List<String>) {
    companion object {
        val handlers: MutableList<MethodHandler> = mutableListOf()

        // Gets from the last available handler
        fun exists(name: String): Boolean {
            return getLastHandler(name) != null
        }
        fun getLastHandler(name: String): MethodHandler? {
            val methodSignature = name.split('.').toMutableList()

            val methodName = methodSignature.removeLast()
            val namespaceName = methodSignature.joinToString(".")

            // Checks starting from innermost handler, then upwards to global handler
            for (handler in handlers.reversed()) {
                if (namespaceName in handler.loadedNamespaces) {
                    val namespace = handler.loadedNamespaces[namespaceName]
                    if (namespace!!.hasMethod(methodName)) return handler
                }
            }
            return null
        }
    }

//    val dataMethod = Method(HSType.STR::class, runnable = {args -> args[0]})
    val loadedNamespaces = mutableMapOf<String, Namespace>()
    init {
        val namespaceMap = mapOf(
            "" to HSNamespace,
            "console" to ConsoleNamespace,
            "math" to MathNamespace,
            "string" to StringNamespace,
            "conditionals" to ConditionalNamespace
        )
        namespaces.forEach { n ->
            // $include hs
            val namespaceName = if (n == "hs") "" else n

            val namespace = namespaceMap[namespaceName] ?: ErrorHandler.report(ReferenceError(n))
            loadedNamespaces[namespaceName] = namespace
        }
        handlers.add(this)
    }
    fun execute(name: String, arguments: List<HSType>): HSType {
        println("Executing $name [ ${arguments.joinToString(",")} ]")

        val methodSignature = name.split('.').toMutableList()
        val methodName = methodSignature.removeLast()
        val namespaceName = methodSignature.joinToString(".")

        // Check if namespace exists/is loaded
        if (namespaceName !in loadedNamespaces) {
            ErrorHandler.report(ReferenceError(namespaceName))
        }
        val namespace = loadedNamespaces[namespaceName]

        // Check if method exists in the namespace
        if (!namespace!!.hasMethod(methodName)) {
            ErrorHandler.report(ReferenceError(methodName))
        }

        // Execute the method
        return namespace.executeMethod(methodName, arguments)
    }
}