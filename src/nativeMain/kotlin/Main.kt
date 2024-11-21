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
        val fileName = args[0]
        val filePath = fileName.toPath()

        // Read file content
        val fileContent = readFileContent(filePath)

        // Parse CLI arguments
        val arguments = parseArguments(args)

        // Run interpreter
        interpInstance = Interpreter(fileContent, arguments.options, arguments.flags, arguments.programArgs)
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
        println("Error reading file: ${e.message}")

        exitProcess(1)
    }
}

data class Arguments(val options: Map<String, List<String>>, val flags: List<String>, val programArgs: List<String>)

private fun parseArguments(args: Array<String>): Arguments {
    val options = mutableMapOf<String, List<String>>()
    val flags = mutableListOf<String>()
    val programArgs = mutableListOf<String>()

    for (arg in args) {
        val keyValue = arg.split('=')
        // Parse program options (--option=value1,value2)
        if (keyValue.size == 2 && keyValue[0].startsWith("--")) {
            val key = keyValue[0].removePrefix("--")
            val values = keyValue[1].split(',')
            options[key] = values
        }
        // Parse program flags (-G, -D, -GA, etc.)
        else if (arg.startsWith("-")) {
            flags.add(arg.removePrefix("-"))
        }
        // Parse program arguments
        else programArgs.add(arg)
    }
    return Arguments(options, flags, programArgs)
}