package me.flaming.h0rsescript.parser

import me.flaming.h0rsescript.ast.OperationType

data class Token(
    val type: TokenType = TokenType.COMMENT,
    val value: String = "",
    val position: Int = 0
) {
    companion object {
        const val TAGPREFIX = '#'
        val tags = listOf("IMPORT", "FILE", "MODE", "EXPORT").map { t -> "$TAGPREFIX$t" }
        const val KEYWORDPREFIX = '$'
        val keywords = listOf("define", "expect", "include", "end").map { k -> "$KEYWORDPREFIX$k" }
        val booleans = listOf("TRUE", "FALSE")
        val operatorMap = mapOf(
            OperationType.MUTABLE_CREATE to "->",
            OperationType.IMMUTABLE_CREATE to "<->",
            OperationType.MODIFY_MUTABLE to ">",
            OperationType.INSERT_PIPE to "<",
            OperationType.EXTRACT_PIPE to "<-",
            OperationType.DELETE to "-",
        )
        val operators = operatorMap.values.toList()
        fun getOperatorType(symbol: String): OperationType? {
            return operatorMap.keys.find { k -> operatorMap[k] == symbol }
        }

//        fun validListOf(type: TokenType): List<String>? {
//
//            return when (type) {
//                TokenType.KEYWORD -> keywords
//                TokenType.TAG -> tags
//                TokenType.ASSIGNMENT_OPERATOR -> operators
//                TokenType.BOOLEAN -> booleans
//                TokenType.OPEN_BRACKET -> listOf("[")
//                TokenType.CLOSE_BRACKET -> listOf("]")
//                TokenType.OPEN_CURLY -> listOf("{")
//                TokenType.CLOSE_CURLY -> listOf("}")
//                TokenType.COMMA -> listOf(",")
//                else -> null
//            }
//        }
//
//        fun validChars(type: TokenType): String? {
//            return when (type) {
//                TokenType.IDENTIFIER -> "abcdefghijklmnopqrstuvwxyz123456789_"
//                TokenType.QUALIFIED_IDENTIFIER -> "abcdefghijklmnopqrstuvwxyz1234567890_."
//                else -> null
//            }
//        }
//        fun validPrefixes(type: TokenType): String? {
//            return when (type) {
//                TokenType.IDENTIFIER -> "abcdefghijklmnopqrstuvwxyz123456789_"
//                TokenType.QUALIFIED_IDENTIFIER -> "abcdefghijklmnopqrstuvwxyz1234567890_."
//                TokenType.KEYWORD -> KEYWORDPREFIX.toString()
//                TokenType.TAG -> TAGPREFIX.toString()
//                TokenType.ASSIGNMENT_OPERATOR -> "<->"
//                TokenType.STRING -> "\""
//                TokenType.NUMBER -> "1234567890"
//                TokenType.BOOLEAN -> TODO()
//                TokenType.OPEN_BRACKET -> TODO()
//                TokenType.CLOSE_BRACKET -> TODO()
//                TokenType.OPEN_CURLY -> TODO()
//                TokenType.CLOSE_CURLY -> TODO()
//                TokenType.COMMA -> TODO()
//                TokenType.COMMENT -> TODO()
//            }
//        }
    }
}

enum class TokenType {
    IDENTIFIER,    // Variable or function names
    QUALIFIED_IDENTIFIER, // Function signatures like math.add
    KEYWORD,       // Keywords like $define, $end, etc.
    TAG,           // Tags like #IMPORT, #EXPORT, #LIBRARY
    OPERATOR,        // Symbols like ->, <->, etc.

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