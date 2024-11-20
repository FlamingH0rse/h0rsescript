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
        STR, NUM, BOOL
    }
    val value = when (type) {
        LiteralType.STR -> unescape(rawValue).trim('"')
        LiteralType.NUM -> rawValue.toDouble()
        LiteralType.BOOL -> rawValue.lowercase().toBooleanStrict()
    }
    private fun unescape(str: String): String {
        var unescapedStr = str
        for ((k,v) in escapeSequences) {
            unescapedStr = unescapedStr.replace(k, v)
        }
        return unescapedStr
    }
}