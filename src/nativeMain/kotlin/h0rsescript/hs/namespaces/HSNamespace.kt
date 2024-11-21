package me.flaming.h0rsescript.hs.namespaces

import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.Method
import me.flaming.h0rsescript.hs.Namespace
import kotlin.system.exitProcess

object HSNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "data" to Method (HSType::class, runnable = { args -> args[0] }),
        "type" to Method (HSType::class, runnable = { args -> args[0]::class.simpleName}),
        "exit" to Method (HSType.NUM::class) { args ->
            val exitCode = (args[0] as HSType.NUM).value
            exitProcess(exitCode.toInt())
        },
        "run" to Method (HSType.FUN::class, HSType.ARRAY::class) { args ->
            val function = args[0] as HSType.FUN
            val arguments = (args[1] as HSType.ARRAY).elements
            me.flaming.interpInstance!!.executeHSFunction(function, arguments)
        }
//        "namespaces" to Method (runnable = {args -> MethodHandler.handlers.last().loadedNamespaces.keys })
    )
}