package me.flaming.h0rsescript.runtime.libraries

import me.flaming.h0rsescript.core.ErrorHandler
import me.flaming.h0rsescript.errors.ParsingError
import me.flaming.h0rsescript.runtime.H0Type
import me.flaming.h0rsescript.runtime.Method
import me.flaming.h0rsescript.runtime.Namespace

object NumberNamespace : Namespace() {
    override val methods: Map<String, Method> = mapOf(
        "to_num" to Method(H0Type.STR::class) { args ->
            val str = (args[0] as H0Type.STR).value
            try {
                return@Method str.toDouble()
            } catch (e: NumberFormatException) {
                ErrorHandler.report(ParsingError(str, H0Type.NUM::class))
            }
        }
    )
}