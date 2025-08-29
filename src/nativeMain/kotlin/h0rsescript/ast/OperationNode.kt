package me.flaming.h0rsescript.ast

enum class OperationType {
    MUTABLE_CREATE,
    IMMUTABLE_CREATE,
    MODIFY_MUTABLE,
    INSERT_PIPE,
    EXTRACT_PIPE,
    DELETE
}

sealed class OperationNode(
    open val operationType: OperationType,
) : ASTNode {
    companion object {
        val validTypes: List<OperationType>
            get() = listOf()
    }
}

data class CreationNode(
    val name: IdentifierNode,
    val value: ASTNode,
    override val operationType: OperationType,

) : OperationNode(operationType) {
    companion object {
        val validTypes =
            listOf(OperationType.MUTABLE_CREATE, OperationType.IMMUTABLE_CREATE, OperationType.MODIFY_MUTABLE)
    }

    init {
        require(operationType in validTypes)
        { "Must be MUTABLE_CREATE or IMMUTABLE_CREATE or MODIFY_MUTABLE" }
    }
}

data class DeletionNode(
    val values: List<IdentifierNode>,

) : OperationNode(OperationType.DELETE) {
    companion object {
        val validTypes = listOf(OperationType.DELETE)
    }
}

data class PipeNode(
    val content: List<ASTNode>,

    ) : OperationNode(OperationType.INSERT_PIPE) {

    companion object {
        val validTypes = listOf(OperationType.INSERT_PIPE)
    }
}

data class PipeExtractionNode(
    val pipe: PipeNode,
    val extractedBy: IdentifierNode,

    ) : OperationNode(OperationType.EXTRACT_PIPE) {

    companion object {
        val validTypes = listOf(OperationType.EXTRACT_PIPE)
    }
}