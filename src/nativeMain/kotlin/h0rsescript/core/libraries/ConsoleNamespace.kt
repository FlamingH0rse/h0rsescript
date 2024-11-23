package me.flaming.h0rsescript.core.namespaces

import me.flaming.h0rsescript.core.H0Type
import me.flaming.h0rsescript.core.Method
import me.flaming.h0rsescript.core.Namespace

object ConsoleNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "write" to Method(H0Type::class) { args ->
            print(args[0])
        },
        "write_line" to Method(H0Type::class) { args ->
            println(args[0])
        },
        "debug" to Method(H0Type::class) { args ->
            println("[DEBUG] ${args[0]}")
        },
        "read" to Method(H0Type.STR::class) { args ->
            print((args[0] as H0Type.STR).value)
            val userInput = readln()
            userInput
        },
        "read_line" to Method(H0Type.STR::class) { args ->
            print((args[0] as H0Type.STR).value + "\n")
            val userInput = readln()
            userInput
        },
        "clear" to Method() { _ ->
            print("\u001b[H\u001b[2J")
        }
    )
}
