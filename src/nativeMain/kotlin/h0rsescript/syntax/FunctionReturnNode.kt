package me.flaming.h0rsescript.syntax

data class FunctionReturnNode(val returnValue: ASTNode = IdentifierNode("null")): ASTNode {
}