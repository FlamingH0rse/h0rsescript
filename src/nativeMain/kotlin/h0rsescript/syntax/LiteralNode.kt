package me.flaming.h0rsescript.syntax

data class LiteralNode (val rawValue: String = "", val type: LiteralType, val list: List<ASTNode> = listOf()): ASTNode {
    private val escapeSequences = mapOf(
        "\\\\" to "\\",
        "\\n" to "\n",
        "\\t" to "\t",
        "\\r" to "\r",
        "\\b" to "\b",
        "\\\"" to "\""
    )
    enum class LiteralType {
        STR, NUM, BOOL, ARRAY
    }
    val value: Any = when (type) {
        LiteralType.STR -> unescape(rawValue).trim('"')       // String
        LiteralType.NUM -> rawValue.toDouble()                       // Double
        LiteralType.BOOL -> rawValue.lowercase().toBooleanStrict()   // Boolean
        LiteralType.ARRAY -> list                                    // List<ASTNode> needs to be parsed during runtime
    }
    private fun unescape(str: String): String {
        var unescapedStr = str
        for ((k,v) in escapeSequences) {
            unescapedStr = unescapedStr.replace(k, v)
        }
        return unescapedStr
    }
}