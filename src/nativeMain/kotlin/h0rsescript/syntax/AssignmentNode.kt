package me.flaming.h0rsescript.syntax

data class AssignmentNode(val name: String, val value: ASTNode, val assignmentType: AssignmentType): ASTNode {
    enum class AssignmentType {
        VARIABLE, CONSTANT, EDIT
    }
}