package me.flaming

import me.flaming.h0rsescript.Interpreter
import me.flaming.h0rsescript.Logger
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import kotlin.system.exitProcess
import kotlin.time.TimeSource

const val LANG_NAME = "h0rsescript"
const val LANG_NAME_SHORT = "h0"
const val VERSION = "0.1.0"

var interpInstance: Interpreter? = null
var logger = Logger()
var timeStart = TimeSource.Monotonic.markNow()

val optionsList = mapOf(
    "-version" to "Display current h0 version",
    "-help" to "Display available commands and flags",
    "--parser-options=" to "Pass options for the parser\nAvailable values: [log-tokens, log-function-defines, log-function-calls]",
    "-log-interp-times" to "Display the times taken by the tokenizer, parser and interpreter",
    "--log-file=" to "Log errors, warnings and info to a file"
)

val commandsHelp = "Usage: $LANG_NAME_SHORT [options] <file_name> [arguments]\n\nAvailable options:" + optionsList.map { (c, d)-> "$c      $d"}.joinToString("\n")

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

        // Parse CLI arguments
        val arguments = parseArguments(args)

        val options = arguments.options
        val fileName = arguments.fileName
        val programArgs = arguments.programArgs

        // Check version
        if (options.containsKey("version")) return println("$LANG_NAME current version: $VERSION")
        // Display available options
        if (options.containsKey("help")) return println(commandsHelp)

        // Create logger instance
        if ("log-file" in options) {
            val logFileName = options["log-file"]!![0] + ".log"
            logger = Logger(logFileName.toPath())
            println("${logFileName.toPath()} ok")
        }
        else logger = Logger()

        // Run .h0 file
        if (fileName == "") return println("Error: Please specify file name to run")
        val filePath = fileName.toPath()
        val fileContent = readFileContent(filePath)

        // Run interpreter
        timeStart = TimeSource.Monotonic.markNow()
        interpInstance = Interpreter(fileContent, options, programArgs)
        interpInstance!!.run()
    }
}

private fun readFileContent(filePath: Path) : String {
    try {
        val fileContent = FileSystem.SYSTEM.read(filePath) {
            readUtf8()
        }
        return fileContent
    } catch (e: IOException) {
        logger.logln("Error reading file: ${e.message}", Logger.Log.ERROR)

        exitProcess(1)
    }
}

data class Arguments(val options: Map<String, List<String>>, val fileName: String, val programArgs: List<String>)

private fun parseArguments(args: Array<String>): Arguments {
    val options = mutableMapOf<String, List<String>>()
    val programArgs = mutableListOf<String>()


    var fileName: String? = null

    for (arg in args) {
        // Parse program options (--option=value1,value2)
        if (arg.startsWith("--")) {
            val keyValue = arg.split('=')
            val key = keyValue[0].removePrefix("--")
            val values = (keyValue.getOrNull(1)?:"").split(',')
            options[key] = values
        }
        // Parse program options without values (-help, -version)
        else if (arg.startsWith("-")) {
            val option = arg.removePrefix("-")
            options[option] = listOf()
        }
        // Parse file name (main.h0)
        else if (fileName == null) fileName = arg
        // Parse program arguments
        else programArgs.add(arg)
    }
    return Arguments(options, fileName ?: "", programArgs)
}