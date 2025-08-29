package me.flaming.h0rsescript.errors

import me.flaming.h0rsescript.ast.IdentifierNode

class ReferenceError(identifier: IdentifierNode) : H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "ReferenceError"
        super.message = "Unexpected identifier '${identifier.identifier}'"
    }
}