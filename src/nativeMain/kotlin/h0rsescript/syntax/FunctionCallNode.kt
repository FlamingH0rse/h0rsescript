package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class FunctionCallNode(val name: IdentifierNode, val arguments: List<ASTNode>): ASTNode {
}