package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.ParsedCode

data class FunctionDefNode(
    val name: IdentifierNode,
    val options: Map<IdentifierNode, List<IdentifierNode>>,
    val body: ParsedCode,

) : ASTNode