package me.flaming.h0rsescript

import me.flaming.h0Process
import okio.FileSystem
import okio.IOException
import okio.Path
import kotlin.time.Duration
import kotlin.time.TimeSource

class Logger(private val logFilePath: Path? = null) {
    private val logStack = mutableMapOf<Duration, String>(
        Duration.ZERO to "[START]"
    )
    enum class Log {
        NONE,
        INFO,
        WARNING,
        ERROR
    }

    fun logln(str: String, logType: Log = Log.NONE, logInFile: Boolean = true) {
        log(str + "\n", logType, logInFile)
    }

    fun log(str: String, logType: Log = Log.NONE, logInFile: Boolean = true) {
        var logString = when (logType) {
            Log.INFO -> "[INFO] "
            Log.WARNING -> "[WARNING] "
            Log.ERROR -> "[ERROR] "
            Log.NONE -> ""
        }
        logString += str

        print(logString)

        if (logFilePath == null || !logInFile) return
        // Store log time and log string in stack
        val logTime = TimeSource.Monotonic.markNow().minus(h0Process.timeStart)
        logStack[logTime] = logString
    }

    fun createLogFile() {
        // Marking end of runtime
        val endTime = TimeSource.Monotonic.markNow().minus(h0Process.timeStart)
        logStack[endTime] = "[END]"

        // Formatting logs
        val logs = logStack.map { (logTime, str) ->
            val timestamp = getTimestamp(logTime)
            val trimmedStr = str.removeSuffix("\n")

            "$timestamp $trimmedStr"
        }.joinToString("\n")

        // Create and write to log file
        try {
            if (logFilePath == null) return
            FileSystem.SYSTEM.write(logFilePath) {
                writeUtf8(logs)
            }
            println("Log file created at '${FileSystem.SYSTEM.canonicalize(logFilePath)}'")
        } catch (e: IOException) {
            logln("Error writing log file: ${e.message}", Log.ERROR, false)
        }
    }

    private fun getTimestamp(logTime: Duration): String {
        val hrs = logTime.inWholeHours
        val mins = logTime.inWholeMinutes % 60
        val secs = logTime.inWholeSeconds % 60
        val ms = logTime.inWholeMilliseconds % 1000

        return "${paddedTime(hrs)}:${paddedTime(mins)}:${paddedTime(secs)}.${paddedTime(ms, 3)}"
    }
    private fun paddedTime(time: Long, length: Int = 2) = time.toString().padStart(length, '0')
}