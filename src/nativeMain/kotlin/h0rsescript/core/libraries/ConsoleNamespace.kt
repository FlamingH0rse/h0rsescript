package me.flaming.h0rsescript.core.libraries

import me.flaming.h0rsescript.core.H0Type
import me.flaming.h0rsescript.core.Method
import me.flaming.h0rsescript.core.Namespace
import me.flaming.logger

object ConsoleNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "write" to Method(H0Type::class) { args ->
            logger.log(args[0].toString())
        },
        "write_line" to Method(H0Type::class) { args ->
            logger.logln(args[0].toString())
        },
        "debug" to Method(H0Type::class) { args ->
            logger.logln("[DEBUG] ${args[0]}")
        },
        "read" to Method(H0Type.STR::class) { args ->
            logger.log((args[0] as H0Type.STR).value)
            val userInput = readln()
            userInput
        },
        "read_line" to Method(H0Type.STR::class) { args ->
            logger.log((args[0] as H0Type.STR).value + "\n")
            val userInput = readln()
            userInput
        },
        "clear" to Method() { _ ->
            logger.logln("\u001b[H\u001b[2J")
        }
    )
}
