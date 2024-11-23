package me.flaming.h0rsescript.ast

data class FunctionReturnNode(val returnValue: ASTNode = IdentifierNode("null")): ASTNode {
}