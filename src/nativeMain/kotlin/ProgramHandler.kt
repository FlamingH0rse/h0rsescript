package me.flaming

import me.flaming.h0rsescript.Interpreter
import me.flaming.h0rsescript.Logger
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import kotlin.system.exitProcess
import kotlin.time.TimeSource

typealias OptionMap = Map<String, List<String>>

var logger = Logger()

class H0Process(args: ProgramArgs) {
    val options = args.options
    val fileName = args.fileName
    val h0Args = args.additionalArgs

    var fileContent = ""
        private set
    val rootPaths = mutableListOf<Path>()

    var timeStart = TimeSource.Monotonic.markNow()
    var interpreterInstance: Interpreter? = null

    fun start() {
        handleOptions()

        // Read .h0 file
        if (fileName == "") return logger.log("Please specify the file name to run", Logger.Log.ERROR)

        val filePath = fileName.toPath()
        val fileExtension = filePath.name.substringAfterLast('.')
        if (fileExtension != LANG_FILE_EXTENSION) logger.logln(
            "Use recommended file extension '.h0'",
            Logger.Log.WARNING
        )

        fileContent = readFileContent(filePath)

        // Run interpreter
        timeStart = TimeSource.Monotonic.markNow()

        interpreterInstance = Interpreter(fileContent, options, h0Args)
        interpreterInstance!!.run()

        // Exit process after execution
        exit(0)
    }

    fun exit(status: Int): Nothing {
        // Create log file
        logger.createLogFile()

        // Exit process
        exitProcess(status)
    }

    fun handleOptions() {
        // Check version
        if (options.containsKey("version")) return logger.logln("$LANG_NAME current version: $VERSION")

        // Display available options
        if (options.containsKey("help")) return logger.logln(commandsHelp)

        // Set root paths
        val defaultPath = getEnv("H0_HOME")?.toPath()
        if (defaultPath != null) rootPaths.add(defaultPath)
        if ("libs" in options) {
            val libraryDirPaths = options["libs"]?.map { p -> p.toPath() } ?: listOf()
            rootPaths.addAll(libraryDirPaths)
        }

        // Set log file path if any
        logger = Logger(getLogFilePath())
    }

    fun getLogFilePath(): Path? {
        if ("log-file" !in options) return null

        val logFileName = options["log-file"]?.get(0) ?: fileName
        return logFileName.toPath()
    }

    fun readFileContent(filePath: Path): String {
        try {
            return FileSystem.SYSTEM.read(filePath) {
                readUtf8()
            }
        } catch (e: IOException) {
            logger.logln("Error reading file: ${e.message}", Logger.Log.ERROR)

            exit(1)
        }
    }
}

data class ProgramArgs(val options: OptionMap, val fileName: String, val additionalArgs: List<String>) {
    companion object {
        fun from(args: Array<String>): ProgramArgs {
            val options = mutableMapOf<String, List<String>>()
            var fileName = ""
            val additionalArgs = mutableListOf<String>()

            for (arg in args) {

                // Parse program options (--log-file=./logs/main.log / --help / --version)
                if (arg.startsWith("--")) {
                    val option = arg.split('=')
                    val optionName = option[0].removePrefix("--").trim()
                    val optionValues = (option.getOrNull(1) ?: "").split(',').map(String::trim)
                    options[optionName] = optionValues
                }

                // Parse program options without values (-help, -version)
                else if (arg.startsWith("-")) {
                    val option = arg.removePrefix("-").trim()
                    options[option] = listOf()
                }

                // Parse file name (main.h0)
                else if (fileName == "") fileName = arg

                // Parse program arguments
                else additionalArgs.add(arg)
            }

            return ProgramArgs(options, fileName, additionalArgs)
        }
    }
}