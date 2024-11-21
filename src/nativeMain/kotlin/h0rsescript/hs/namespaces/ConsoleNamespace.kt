package me.flaming.h0rsescript.hs.namespaces

import me.flaming.h0rsescript.hs.HSType
import me.flaming.h0rsescript.hs.Method
import me.flaming.h0rsescript.hs.Namespace

object ConsoleNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "print" to Method(HSType::class) { args ->
            println(args[0])
        },
        "println" to Method(HSType::class) { args ->
            println(args[0])
        },
        "debug" to Method(HSType::class) { args ->
            println("[DEBUG] ${args[0]}")
        },
        "input" to Method(HSType.STR::class) { args ->
            print((args[0] as HSType.STR).value)
            val userInput = readln()
            userInput
        },
        "clear" to Method() { _ ->
            print("\u001b[H\u001b[2J")
        }
    )
}
