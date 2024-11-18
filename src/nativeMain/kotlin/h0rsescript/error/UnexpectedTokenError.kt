package me.flaming.h0rsescript.error

import me.flaming.h0rsescript.tokens.Token
import me.flaming.h0rsescript.tokens.TokenType

class UnexpectedTokenError(token: Token?, vararg expectedTypes: TokenType, expectedValue: String? = null) : HSError() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "UnexpectedTokenError"
        super.message = "Unexpected token '${token?.value}' of type ${token?.type}, expected a ${expectedValue ?: expectedTypes.toList().joinToString(" or ")}"
        super.line = token?.position?.first
        super.column = token?.position?.second
    }
}