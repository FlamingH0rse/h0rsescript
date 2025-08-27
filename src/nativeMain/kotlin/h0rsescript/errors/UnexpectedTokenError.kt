package me.flaming.h0rsescript.errors

import me.flaming.h0rsescript.parser.Token
import me.flaming.h0rsescript.parser.TokenType
import me.flaming.h0rsescript.parser.Tokenizer

class UnexpectedTokenError(token: Token?, vararg expectedTypes: TokenType, expectedValue: String? = null) : H0Error() {
    init {
        super.type = ErrorType.SYNTAX
        super.name = "UnexpectedTokenError"
        super.message = "Unexpected token '${token?.value}' of type ${token?.type}, expected ${
            expectedValue ?: expectedTypes.toList().joinToString(" or ")
        }"
        super.line = Tokenizer.getLineCol(token?.position ?: 0).first
        super.column = Tokenizer.getLineCol(token?.position ?: 0).second
    }
}