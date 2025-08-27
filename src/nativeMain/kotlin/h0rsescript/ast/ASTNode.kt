package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

interface ASTNode {
    val tokens: List<Token>
}