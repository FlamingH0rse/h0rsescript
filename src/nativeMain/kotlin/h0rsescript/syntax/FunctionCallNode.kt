package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class FunctionCallNode(val name: String, val arguments: List<ASTNode>): ASTNode {
}