package me.flaming

import me.flaming.h0rsescript.Logger
import okio.FileSystem
import okio.IOException
import okio.Path

object FS {
    fun readFileContent(filePath: Path): String {
        try {
            return FileSystem.SYSTEM.read(filePath) {
                readUtf8()
            }
        } catch (e: IOException) {
            logger.logln("Error reading file: ${e.message}", Logger.Log.ERROR)

            h0Process.exit(1)
        }
    }

    fun libNameFromPath(path: Path): String {
        return path.name.removePrefix(LANG_FILE_EXTENSION)
    }

    fun lineColumnFromPos(text: String, pos: Int): Pair<Int, Int> {
        val subStr = text.substring(0, pos)
        val lines = subStr.split('\n')

        val line = lines.size
        val column = lines.last().length + 1

        return Pair(line, column)
    }
}