package me.flaming.h0rsescript.parser

data class Token(
    val type: TokenType = TokenType.COMMENT,
    val value: String = "",
    val position: Int = 0
) {
    companion object {
        const val TPREFIX = '#'
        val tags = listOf("IMPORT", "EXPORT", "TYPING", "LIBRARY").map { t -> "$TPREFIX$t"}
        const val KPREFIX = '$'
        val keywords = listOf("define", "end", "parameters", "return", "mode", "include").map { k -> "$KPREFIX$k"}
        val booleans = listOf("TRUE", "FALSE")
        private val symbols = listOf("->", "<->", ">", "<-", "<")

        fun validListOf(type: TokenType): List<String>? {

            return when (type) {
                TokenType.KEYWORD -> keywords
                TokenType.TAG -> tags
                TokenType.ASSIGNMENT_OPERATOR -> symbols
                TokenType.BOOLEAN -> booleans
                TokenType.OPEN_BRACKET -> listOf("[")
                TokenType.CLOSE_BRACKET -> listOf("]")
                TokenType.OPEN_CURLY -> listOf("{")
                TokenType.CLOSE_CURLY -> listOf("}")
                TokenType.COMMA -> listOf(",")
                else -> null
            }
        }
    }
}

enum class TokenType {
    IDENTIFIER,    // Variable or function names
    QUALIFIED_IDENTIFIER, // Function signatures like math.add
    KEYWORD,       // Keywords like $define, $end, etc.
    TAG,           // Tags like #IMPORT, #EXPORT, #LIBRARY
    ASSIGNMENT_OPERATOR,        // Symbols like ->, <->, etc.

    STRING,        // String literals
    NUMBER,        // Numeric literals
    BOOLEAN,       // TRUE/FALSE

    OPEN_BRACKET,  // [
    CLOSE_BRACKET, // ]
    OPEN_CURLY,    // {
    CLOSE_CURLY,   // }
    COMMA,         // ,

    COMMENT,       // Comments
}