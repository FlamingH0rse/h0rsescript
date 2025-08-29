package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token
import me.flaming.h0rsescript.parser.TokenType


data class IdentifierNode(
    private val token: Token,

    ) : ASTNode, IdentifierOrLiteralNode {
    val identifier = token.value

    var tokens = listOf(token)
        private set
    var qualified = false
        private set

    init {
        if (token.type == TokenType.QUALIFIED_IDENTIFIER) {
            qualified = true

            val startPos = token.position
            val ids = token.value.split(".")

            tokens = ids.mapIndexed { i, value ->

                val pos = if (i == 0) startPos else startPos + ids[i - 1].length + 1

                Token(TokenType.IDENTIFIER, value, pos)

            }
        }

    }
}