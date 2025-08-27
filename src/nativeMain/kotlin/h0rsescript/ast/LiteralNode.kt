package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token


sealed class LiteralNode(
    override val tokens: List<Token>
) : ASTNode, IdentifierOrLiteralNode {
    abstract val value: Any

    data class Str(
        val token: Token

    ) : LiteralNode(listOf(token)) {
        override val value = unescape(token.value).trim('"')

        companion object {
            private val escapeSequences = mapOf(
                "\\\\" to "\\",
                "\\n" to "\n",
                "\\t" to "\t",
                "\\r" to "\r",
                "\\b" to "\b",
                "\\\"" to "\""
            )

            private fun unescape(str: kotlin.String): kotlin.String {
                var unescapedStr = str
                for ((k, v) in escapeSequences) {
                    unescapedStr = unescapedStr.replace(k, v)
                }
                return unescapedStr
            }
        }
    }

    data class Num(
        val token: Token

    ) : LiteralNode(listOf(token)) {

        override val value = token.value.toDouble()

    }

    data class Bool(
        val token: Token

    ) : LiteralNode(listOf(token)) {

        override val value = token.value.lowercase().toBooleanStrict()

    }

    data class Array(

        override val tokens: List<Token>,
        val list: List<IdentifierOrLiteralNode>

    ) : LiteralNode(tokens) {

        override val value = list

    }
}