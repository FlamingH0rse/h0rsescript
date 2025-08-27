package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

data class DeclarativeNode(
    val key: String,
    val values: List<String>,
    override val tokens: List<Token>
) : ASTNode
