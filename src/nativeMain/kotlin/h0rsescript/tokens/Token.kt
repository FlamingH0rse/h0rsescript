package me.flaming.h0rsescript.tokens

data class Token(
    val type: TokenType = TokenType.COMMENT,
    val value: String = "",
    val position: Pair<Int, Int> = Pair(0, 0),
    val absolutePosition: Int = 0
)
