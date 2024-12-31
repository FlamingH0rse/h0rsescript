package me.flaming.h0rsescript.core

import me.flaming.h0rsescript.errors.H0Error
import me.flaming.logger

object ErrorHandler {
    fun report(error: H0Error): Nothing {
        val message = error.getMessage()
        println(message)
        exitProcess(1)
        logger.logln(message, Logger.Log.ERROR)
    }
}