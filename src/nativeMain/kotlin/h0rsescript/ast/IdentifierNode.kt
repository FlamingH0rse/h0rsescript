package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

interface IdentifierOrLiteralNode : ASTNode

data class IdentifierNode(
    val token: Token
) : ASTNode, IdentifierOrLiteralNode {
    override val tokens = listOf(token)
    val identifier = token.value
}