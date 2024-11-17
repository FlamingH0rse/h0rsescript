package me.flaming.h0rsescript.syntax

import me.flaming.h0rsescript.SyntaxTrees.IdentifierNode

data class AssignmentNode(val name: IdentifierNode, val value: ASTNode, val locked: Boolean = false): ASTNode {
}