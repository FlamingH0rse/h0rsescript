package me.flaming.h0rsescript.syntax

data class LiteralNode (val rawValue: String, val type: LiteralType): ASTNode {
    private val escapeSequences = mapOf(
        "\\\\" to "\\",
        "\\n" to "\n",
        "\\t" to "\t",
        "\\r" to "\r",
        "\\b" to "\b",
        "\\\"" to "\""
    )
    enum class LiteralType {
        STRING, NUMBER, BOOLEAN
    }
    val value = when (type) {
        LiteralType.STRING -> unescape(rawValue).trim('"')
        LiteralType.NUMBER -> rawValue.toDouble()
        LiteralType.BOOLEAN -> rawValue.lowercase().toBooleanStrict()
    }
    private fun unescape(str: String): String {
        var unescapedStr = str
        for ((k,v) in escapeSequences) {
            unescapedStr = unescapedStr.replace(k, v)
        }
        return unescapedStr
    }
}