package me.flaming.h0rsescript.errors

import me.flaming.h0rsescript.parser.Token
import me.flaming.h0rsescript.parser.TokenType

class UnexpectedTokenError(token: Token?, vararg expectedTypes: TokenType, expectedValue: String? = null) : H0Error() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "UnexpectedTokenError"
        super.message = "Unexpected token '${token?.value}' of type ${token?.type}, expected a ${expectedValue ?: expectedTypes.toList().joinToString(" or ")}"
        super.line = token?.position?.first
        super.column = token?.position?.second
    }
}