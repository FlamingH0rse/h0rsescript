package me.flaming

import me.flaming.h0rsescript.Interpreter
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import kotlin.system.exitProcess

const val LANG_NAME = "h0rsescript"
const val VERSION = "1.0.0"

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
        val options = getOptions(args)
        val fileName = args[0]
        val filePath = fileName.toPath()

        val fileContent = readFileContent(filePath)

        // Run interpreter
        val hsProgram = Interpreter(fileContent, options)
        hsProgram.run()
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

private fun getOptions(args: Array<String>): Map<String, List<String>> {
    val options: MutableMap<String, List<String>> = mutableMapOf()
    for (arg in args) {
        val KV = arg.split('=')
        if (KV.size != 2 || !KV[0].startsWith("--")) continue
        val key = KV[0].removePrefix("--")
        val values = KV[1].split(',')
        options[key] = values
    }
    return options
}