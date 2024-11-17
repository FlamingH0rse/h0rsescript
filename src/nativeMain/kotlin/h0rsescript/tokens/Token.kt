package me.flaming.h0rsescript.tokens

data class Token(val type: TokenType, val value: String, val position: Pair<Int, Int>, val absolutePosition: Int)
