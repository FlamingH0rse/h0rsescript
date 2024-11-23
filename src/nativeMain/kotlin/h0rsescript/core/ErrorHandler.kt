package me.flaming.h0rsescript.core

import me.flaming.h0rsescript.errors.H0Error
import kotlin.system.exitProcess

object ErrorHandler {
    fun report(error: H0Error): Nothing {
        val message = error.getMessage()
        println(message)
        exitProcess(1)
    }
}