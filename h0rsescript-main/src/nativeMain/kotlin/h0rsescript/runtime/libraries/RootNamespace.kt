package me.flaming.h0rsescript.runtime.libraries

import me.flaming.exit
import me.flaming.h0rsescript.runtime.H0Type
import me.flaming.h0rsescript.runtime.Method
import me.flaming.h0rsescript.runtime.Namespace
import me.flaming.interpInstance

object RootNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "data" to Method (H0Type::class, runnable = { args -> args[0] }),
        "type" to Method (H0Type::class, runnable = { args -> args[0]::class.simpleName}),
        "exit" to Method (H0Type.NUM::class) { args ->
            val exitCode = (args[0] as H0Type.NUM).value
            exit(exitCode.toInt())
        },
        "run" to Method (H0Type.FUN::class, H0Type.ARRAY::class) { args ->
            val function = args[0] as H0Type.FUN
            val arguments = (args[1] as H0Type.ARRAY).elements
            interpInstance!!.executeH0Function(function, arguments)
        }
//        "namespaces" to Method (runnable = {args -> MethodHandler.handlers.last().loadedNamespaces.keys })
    )
}