package me.flaming.h0rsescript.ast

import me.flaming.h0rsescript.parser.Token

enum class OperationType {
    MUTABLE_CREATE,
    IMMUTABLE_CREATE,
    MODIFY_MUTABLE,
    INSERT_PIPE,
    EXTRACT_PIPE,
    DELETE
}

abstract class OperationNode(
    open val operationType: OperationType,
) : ASTNode {
    companion object {
        val validTypes: List<OperationType>
            get() = listOf()
    }
}

data class CreationNode(
    val name: String,
    val value: ASTNode,
    override val operationType: OperationType,
    override val tokens: List<Token>

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
    override val tokens: List<Token>

) : OperationNode(OperationType.DELETE) {
    companion object {
        val validTypes = listOf(OperationType.DELETE)
    }
}

data class PipeNode(
    val content: List<ASTNode>,
    override val tokens: List<Token>,

    ) : OperationNode(OperationType.INSERT_PIPE) {

    companion object {
        val validTypes = listOf(OperationType.INSERT_PIPE)
    }
}

data class PipeExtractionNode(
    val pipeContent: PipeNode,
    val extractedBy: IdentifierNode,
    override val tokens: List<Token>,

    ) : OperationNode(OperationType.EXTRACT_PIPE) {

    companion object {
        val validTypes = listOf(OperationType.EXTRACT_PIPE)
    }
}