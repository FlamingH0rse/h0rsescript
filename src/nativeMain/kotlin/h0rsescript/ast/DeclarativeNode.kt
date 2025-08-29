package me.flaming.h0rsescript.ast

data class DeclarativeNode(
    val key: IdentifierNode,
    val rawValue: LiteralNode.Str,
) : ASTNode
