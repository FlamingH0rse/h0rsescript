package me.flaming.h0rsescript.syntax

import me.flaming.h0rsescript.SyntaxTrees.IdentifierNode

data class AssignmentNode(val name: String, val value: ASTNode, val assignmentType: AssignmentType): ASTNode {
    enum class AssignmentType {
        VARIABLE, CONSTANT, EDIT
    }
}