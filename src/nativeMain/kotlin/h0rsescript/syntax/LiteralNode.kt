package me.flaming.h0rsescript.SyntaxTrees

import me.flaming.h0rsescript.syntax.ASTNode

data class LiteralNode (val rawValue: String, val type: LiteralType): ASTNode {
    enum class LiteralType {
        STRING, NUMBER, BOOLEAN
    }
    val value = when (type) {
        LiteralType.STRING -> rawValue
        LiteralType.NUMBER -> rawValue.toDouble()
        LiteralType.BOOLEAN -> rawValue.lowercase().toBooleanStrict()
    }
}