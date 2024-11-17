package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class IdentifierNode(val name: String) : ASTNode {
}