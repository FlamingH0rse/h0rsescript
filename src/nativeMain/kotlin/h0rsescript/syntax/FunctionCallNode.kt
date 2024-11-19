package me.flaming.h0rsescript.syntax

data class FunctionCallNode(val name: String, val arguments: List<ASTNode>): ASTNode {
}