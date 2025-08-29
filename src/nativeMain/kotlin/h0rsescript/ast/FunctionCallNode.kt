package me.flaming.h0rsescript.ast

data class FunctionCallNode(
    val name: IdentifierNode,
    val arguments: List<ASTNode>,
) : ASTNode