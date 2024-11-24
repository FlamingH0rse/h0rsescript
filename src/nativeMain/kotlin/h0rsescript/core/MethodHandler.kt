package me.flaming.h0rsescript.core

import me.flaming.LANG_NAME_SHORT
import me.flaming.h0rsescript.core.libraries.*
import me.flaming.h0rsescript.errors.ReferenceError

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

//    val dataMethod = Method(H0Type.STR::class, runnable = {args -> args[0]})
    val loadedNamespaces = mutableMapOf<String, Namespace>()
    init {
        val namespaceMap = mapOf(
            "" to RootNamespace,
            "console" to ConsoleNamespace,
            "math" to MathNamespace,
            "string" to StringNamespace,
            "conditionals" to ConditionalNamespace,
            "array" to ArrayNamespace
        )
        namespaces.forEach { n ->
            // $include h0
            val namespaceName = if (n == LANG_NAME_SHORT) "" else n

            val namespace = namespaceMap[namespaceName] ?: ErrorHandler.report(ReferenceError(n))
            loadedNamespaces[namespaceName] = namespace
        }
        handlers.add(this)
    }
    fun execute(name: String, arguments: List<H0Type>): H0Type {
//        println("Executing $name [ ${arguments.joinToString(",")} ]")

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