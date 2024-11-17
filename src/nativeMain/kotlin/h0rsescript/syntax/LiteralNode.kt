package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class LiteralNode (val value: String, val type: LiteralType): ASTNode {
    enum class LiteralType {
        STRING, NUMBER, BOOLEAN
    }
}