package me.flaming.h0rsescript.runtime

import me.flaming.FS
import me.flaming.h0Process
import me.flaming.h0rsescript.Interpreter
import me.flaming.h0rsescript.Logger
import me.flaming.h0rsescript.errors.H0Error
import me.flaming.logger

class ErrorHandler internal constructor(val runtime: Interpreter) {
    fun report(error: H0Error, at: Int?): Nothing {
        // Add the file name and file path
        var message = "Error in ${runtime.fileData.name} (${runtime.fileData.absPath})\n"

        // TODO(Print the exact line where the error occurred)


        // Add the line and column reference
        if (at != null) {
            val lineColumnPair = FS.lineColumnFromPos(runtime.fileData.content, at)
            message += "at line ${lineColumnPair.first} column ${lineColumnPair.second}\n"
        }

        // Add the error message
        message += error.getMessage()

        // Log the error and exit process
        logger.logln(message, Logger.Log.ERROR)
        h0Process.exit(1)
    }
}