package me.flaming.h0rsescript.ast

data class FunctionDefNode(
    val name: IdentifierNode,
    val options: Map<IdentifierNode, List<IdentifierNode>>,
    val body: ParsedCode,

) : ASTNode