package me.flaming

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

const val LANG_NAME = "h0rsescript"
const val LANG_NAME_SHORT = "h0"
const val LANG_FILE_EXTENSION = "h0"
const val VERSION = "0.0.1"

var h0Process = H0Process(ProgramArgs.from(arrayOf()))

val optionsList = mapOf(
    "-version" to "Display current h0 version",
    "-help" to "Display available commands and flags",
    "--parser-options=" to "Pass options for the parser\nAvailable values: [log-tokens, log-function-defines, log-function-calls]",
    "-log-interp-times" to "Display the times taken by the tokenizer, parser and interpreter",
    "--log-file=" to "Log errors, warnings and info to a file",
    "--libs=" to "Path to any H0 libraries to use during runtime"
)
// TODO(print in tabular form)
val commandsHelp = "Usage: $LANG_NAME_SHORT [options] <file_name> [arguments]\n\nAvailable options:\n" + optionsList.map { (c, d)-> "$c      $d"}.joinToString("\n")

// TODO(Implement CLI use)
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Welcome to $LANG_NAME v$VERSION")
        println("Type $LANG_NAME_SHORT <file_name> to run it.")

        while (true) {
            print(">> ")
            val command = readln()
            println("Running $command...")
            // Run interpreter

        }
    } else {

        // Parse arguments and start process
        val arguments = ProgramArgs.from(args)
        val h0Process = H0Process(arguments)

        h0Process.start()

    }
}

@OptIn(ExperimentalForeignApi::class)
fun getEnv(key: String) = getenv(key)?.toKString()