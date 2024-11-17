package me.flaming.h0rsescript

import me.flaming.h0rsescript.error.HSError

object ErrorHandler {
    fun report(error: HSError) {
        val message = error.getMessage()
        println(message)

    }
}