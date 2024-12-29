package me.flaming.h0rsescript.ast

data class AssignmentNode(
    var name: String,
    var assignmentType: AssignmentType,
    var value: ASTNode = FunctionCallNode("data",listOf(IdentifierNode("NULL"))),
    var values: List<ASTNode> = listOf()
): ASTNode {
    enum class AssignmentType {
        VARIABLE, CONSTANT, EDIT, EMPTY, DELETE
    }
}