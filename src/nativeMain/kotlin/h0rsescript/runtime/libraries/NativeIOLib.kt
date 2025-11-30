package me.flaming.h0rsescript.runtime.libraries

import me.flaming.h0rsescript.runtime.H0Type
import me.flaming.h0rsescript.runtime.Method
import me.flaming.h0rsescript.runtime.NativeLibrary
import me.flaming.logger

object NativeIOLib : NativeLibrary() {
    // Sub classes : __native_io.std / __native_io.filesystem
    override val subLibraries = mapOf(
        "std" to IOStdLib,
        "filesystem" to FileIOLib
    )
    object IOStdLib : NativeLibrary() {
        override val methods = mapOf(
            "input" to Method(H0Type.STR::class) { args ->
                logger.log((args[0] as H0Type.STR).value)
                val userInput = readln()
                userInput
            },
            "output" to Method(H0Type::class) { args ->
                logger.log(args[0].toString())
            }
        )
    }
    object FileIOLib : NativeLibrary()
}