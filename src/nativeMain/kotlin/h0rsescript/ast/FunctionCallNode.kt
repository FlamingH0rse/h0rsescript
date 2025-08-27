package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

data class FunctionCallNode(
    val name: String,
    val arguments: List<ASTNode>,
    override val tokens: List<Token>
) : ASTNode