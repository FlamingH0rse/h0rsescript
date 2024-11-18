package me.flaming.h0rsescript

import me.flaming.h0rsescript.error.HSError
import kotlin.system.exitProcess

object ErrorHandler {
    fun report(error: HSError): Nothing {
        val message = error.getMessage()
        println(message)
        exitProcess(1)
    }
}