package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

data class DeclarativeNode(
    val key: String,
    val rawValue: LiteralNode.Str,
    override val tokens: List<Token>
) : ASTNode {

    val values = rawValue.value.split(";")
}
