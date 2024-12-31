package me.flaming.h0rsescript.core

import me.flaming.exit
import me.flaming.h0rsescript.Logger
import me.flaming.h0rsescript.errors.H0Error
import me.flaming.logger

object ErrorHandler {
    fun report(error: H0Error): Nothing {
        val message = error.getMessage()
        logger.logln(message, Logger.Log.ERROR)
        exit(1)
    }
}