package me.flaming.h0rsescript.errors

import me.flaming.h0rsescript.ast.IdentifierNode

class IllegalAssignmentError(variableNode: IdentifierNode) : H0Error() {
    init {
        super.type = ErrorType.RUNTIME
        super.name = "IllegalAssignmentError"
        super.message = "Cannot assign value to '${variableNode.identifier}'"
    }
}