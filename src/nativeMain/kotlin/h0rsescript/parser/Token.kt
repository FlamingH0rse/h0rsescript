package me.flaming.h0rsescript.parser

data class Token(
    val type: TokenType = TokenType.COMMENT,
    val value: String = "",
    val position: Int = 0
)
