package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

data class FunctionDefNode(
    val name: String,
    val options: Map<String, List<String>>,
    val body: List<ASTNode>,
    override val tokens: List<Token>

) : ASTNode