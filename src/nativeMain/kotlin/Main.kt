package me.flaming

import me.flaming.h0rsescript.Interpreter
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import kotlin.system.exitProcess

const val LANG_NAME = "h0rsescript"
const val VERSION = "0.1.0"

var interpInstance: Interpreter? = null

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Welcome to $LANG_NAME v$VERSION")
        println("Type hs <file_name> to run it.")

        while (true) {
            print(">> ")
            val command = readln()
            println("Running $command...")
            // Run interpreter

        }
    } else {
        // Parse CLI arguments
        val arguments = parseArguments(args)

        val command = arguments.command
        val programArgs = arguments.programArgs
        val parameters = arguments.parameters

        // Check if valid command
        if (command !in commands) {
            println(commands.map {(c, d)-> "$c      $d"}.joinToString("\n"))
            return
        }

        // Run command
        when (command) {
            "run" -> {
                val fileName = programArgs[0]
                val filePath = fileName.toPath()

                // Read file content
                val fileContent = readFileContent(filePath)
                // Run interpreter
                interpInstance = Interpreter(fileContent, parameters, programArgs)
                interpInstance!!.run()
            }
            "version" -> {
                println("$LANG_NAME \n Current version: $VERSION")
            }
            "help" -> println(commands.map {(c, d)-> "$c      $d"}.joinToString("\n"))
        }

    }
}

private fun readFileContent(filePath: Path) : String {
    try {
        val fileContent = FileSystem.SYSTEM.read(filePath) {
            readUtf8()
        }
        return fileContent
    } catch (e: IOException) {
        println("Error reading file: ${e.message}")

        exitProcess(1)
    }
}

data class Arguments(val parameters: Map<String, List<String>>, val command: String, val programArgs: List<String>)

val commands = mapOf(
    "run" to "Run a HS file",
    "version" to "Display current HS version",
    "help" to "Display available commands and flags"
)

private fun parseArguments(args: Array<String>): Arguments {
    val options = mutableMapOf<String, List<String>>()
    val programArgs = mutableListOf<String>()

    // Parse command (run, version, etc.)
    val command = args[0]
    for (arg in args) {
        // Parse program options (--option=value1,value2)
        if (arg.startsWith("--")) {
            val keyValue = arg.split('=')
            val key = keyValue[0].removePrefix("--")
            val values = keyValue[1].split(',')
            options[key] = values
        }
        // Parse program arguments
        else programArgs.add(arg)
    }
    return Arguments(options, command, programArgs)
}