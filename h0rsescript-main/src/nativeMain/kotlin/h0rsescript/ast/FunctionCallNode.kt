package me.flaming.h0rsescript.ast

data class FunctionCallNode(val name: String, val arguments: List<ASTNode>): ASTNode {
}